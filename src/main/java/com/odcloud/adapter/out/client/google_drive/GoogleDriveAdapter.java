package com.odcloud.adapter.out.client.google_drive;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_DELETE_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_UPDATE_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_UPLOAD_ERROR;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.odcloud.application.file.port.out.GoogleDrivePort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class GoogleDriveAdapter implements GoogleDrivePort {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final String APPLICATION_NAME = "od-cloud-backup";
    // 모든 그룹 백업 폴더가 생성되는 Drive 최상위 폴더
    private static final String BACKUP_ROOT_FOLDER_NAME = "모이미즘 백업";

    private static final int RETRY_INITIAL_INTERVAL_MILLIS = 1_000;
    private static final int RETRY_MAX_INTERVAL_MILLIS = 16_000;
    private static final int RETRY_MAX_ELAPSED_MILLIS = 60_000;

    private final String shareEmail;
    private final Drive drive;
    private String backupRootFolderId;

    GoogleDriveAdapter(ProfileConstant profileConstant) throws IOException, GeneralSecurityException {
        this.shareEmail = profileConstant.googleDrive().shareEmail();
        this.drive = new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer(createCredentials(profileConstant))
        ).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Refresh Token 방식과 Service Account 방식 모두 credentials 를 한 번만 생성해 재사용한다.
     * Access Token 은 만료 시점에 라이브러리가 자동으로 갱신한다.
     */
    private GoogleCredentials createCredentials(ProfileConstant profileConstant) throws IOException {
        String userRefreshToken = profileConstant.googleDrive().userRefreshToken();
        if (userRefreshToken != null && !userRefreshToken.isBlank()) {
            return UserCredentials.newBuilder()
                .setClientId(profileConstant.googleDrive().clientId())
                .setClientSecret(profileConstant.googleDrive().clientSecret())
                .setRefreshToken(userRefreshToken)
                .build();
        }

        byte[] keyBytes = profileConstant.googleDrive().serviceAccountKeyJson()
            .getBytes(StandardCharsets.UTF_8);
        return GoogleCredentials
            .fromStream(new ByteArrayInputStream(keyBytes))
            .createScoped(Collections.singletonList(DriveScopes.DRIVE));
    }

    /**
     * 호출 제한(429, 403 rateLimitExceeded) 과 5xx 응답은 지수 백오프로 재시도한다.
     * 401 응답은 토큰 갱신을 위해 credentialsAdapter 가 먼저 처리한다.
     */
    private HttpRequestInitializer requestInitializer(GoogleCredentials credentials) {
        HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(credentials);
        return request -> {
            credentialsAdapter.initialize(request);
            HttpBackOffUnsuccessfulResponseHandler backOffHandler =
                new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff.Builder()
                    .setInitialIntervalMillis(RETRY_INITIAL_INTERVAL_MILLIS)
                    .setMaxIntervalMillis(RETRY_MAX_INTERVAL_MILLIS)
                    .setMaxElapsedTimeMillis(RETRY_MAX_ELAPSED_MILLIS)
                    .build())
                    .setBackOffRequired(GoogleDriveAdapter::isRetryable);
            request.setUnsuccessfulResponseHandler((req, res, supportsRetry) ->
                credentialsAdapter.handleResponse(req, res, supportsRetry)
                    || backOffHandler.handleResponse(req, res, supportsRetry));
        };
    }

    private static boolean isRetryable(HttpResponse response) {
        int statusCode = response.getStatusCode();
        if (statusCode == 429 || statusCode / 100 == 5) {
            log.warn("[GoogleDriveAdapter] Drive API 재시도 - statusCode={}", statusCode);
            return true;
        }
        if (statusCode != 403) {
            return false;
        }

        try {
            String body = response.parseAsString();
            boolean rateLimited = body.contains("rateLimitExceeded")
                || body.contains("userRateLimitExceeded");
            if (rateLimited) {
                log.warn("[GoogleDriveAdapter] Drive API 호출 제한 재시도 - statusCode={}", statusCode);
            } else {
                log.warn("[GoogleDriveAdapter] Drive API 권한 오류 - body={}", body);
            }
            return rateLimited;
        } catch (IOException e) {
            return false;
        }
    }

    private void shareWithEmail(Drive drive, String folderId) {
        if (shareEmail == null || shareEmail.isBlank()) {
            return;
        }
        try {
            Permission permission = new Permission()
                .setType("user")
                .setRole("writer")
                .setEmailAddress(shareEmail);
            drive.permissions().create(folderId, permission)
                .setSendNotificationEmail(false)
                .execute();
            log.info("[GoogleDriveAdapter] 폴더 공유 완료 - folderId={}, email={}", folderId, shareEmail);
        } catch (IOException e) {
            log.warn("[GoogleDriveAdapter] 폴더 공유 실패 - folderId={}, email={}, error={}", folderId, shareEmail, e.getMessage());
        }
    }

    /**
     * 그룹 폴더는 최상위 백업 폴더(모이미즘 백업) 하위에 생성한다.
     */
    @Override
    public String ensureFolder(String folderName) {
        return ensureSubFolder(ensureBackupRootFolder(), folderName);
    }

    private synchronized String ensureBackupRootFolder() {
        if (backupRootFolderId == null) {
            backupRootFolderId = ensureRootFolder(BACKUP_ROOT_FOLDER_NAME);
        }
        return backupRootFolderId;
    }

    private String ensureRootFolder(String folderName) {
        try {
            String query = String.format(
                "name='%s' and mimeType='%s' and trashed=false",
                folderName, FOLDER_MIME_TYPE
            );

            FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

            List<File> files = result.getFiles();
            if (files != null && !files.isEmpty()) {
                log.info("[GoogleDriveAdapter] 기존 Drive 폴더 사용 - folderName={}, folderId={}",
                    folderName, files.get(0).getId());
                return files.get(0).getId();
            }

            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            folderMetadata.setMimeType(FOLDER_MIME_TYPE);

            File createdFolder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

            log.info("[GoogleDriveAdapter] Drive 폴더 신규 생성 - folderName={}, folderId={}",
                folderName, createdFolder.getId());
            shareWithEmail(drive, createdFolder.getId());
            return createdFolder.getId();

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 폴더 생성/조회 실패 - folderName={}, error={}",
                folderName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
    }

    @Override
    public String ensureSubFolder(String parentFolderId, String folderName) {
        try {
            String query = String.format(
                "name='%s' and mimeType='%s' and '%s' in parents and trashed=false",
                folderName, FOLDER_MIME_TYPE, parentFolderId
            );

            FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

            List<File> files = result.getFiles();
            if (files != null && !files.isEmpty()) {
                log.info("[GoogleDriveAdapter] 기존 Drive 서브폴더 사용 - folderName={}, folderId={}",
                    folderName, files.get(0).getId());
                return files.get(0).getId();
            }

            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            folderMetadata.setMimeType(FOLDER_MIME_TYPE);
            folderMetadata.setParents(Collections.singletonList(parentFolderId));

            File createdFolder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

            log.info("[GoogleDriveAdapter] Drive 서브폴더 신규 생성 - parentId={}, folderName={}, folderId={}",
                parentFolderId, folderName, createdFolder.getId());
            shareWithEmail(drive, createdFolder.getId());
            return createdFolder.getId();

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 서브폴더 생성/조회 실패 - parentId={}, folderName={}, error={}",
                parentFolderId, folderName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
    }

    @Override
    public String findFolder(String parentFolderId, String folderName) {
        try {
            String query = String.format(
                "name='%s' and mimeType='%s' and '%s' in parents and trashed=false",
                folderName, FOLDER_MIME_TYPE, parentFolderId
            );

            FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                return null;
            }
            return files.get(0).getId();

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 폴더 조회 실패 - parentFolderId={}, folderName={}, error={}",
                parentFolderId, folderName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
    }

    @Override
    public void renameFolder(String folderId, String newName) {
        try {
            File folderMetadata = new File();
            folderMetadata.setName(newName);

            drive.files().update(folderId, folderMetadata)
                .setFields("id")
                .execute();

            log.info("[GoogleDriveAdapter] Drive 폴더 이름 변경 완료 - folderId={}, newName={}",
                folderId, newName);

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 폴더 이름 변경 실패 - folderId={}, newName={}, error={}",
                folderId, newName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_UPDATE_ERROR);
        }
    }

    @Override
    public void moveFolder(String folderId, String newParentFolderId) {
        try {
            File current = drive.files().get(folderId)
                .setFields("parents")
                .execute();
            String previousParents = current.getParents() == null
                ? ""
                : String.join(",", current.getParents());

            drive.files().update(folderId, null)
                .setAddParents(newParentFolderId)
                .setRemoveParents(previousParents)
                .setFields("id, parents")
                .execute();

            log.info("[GoogleDriveAdapter] Drive 폴더 이동 완료 - folderId={}, newParentFolderId={}",
                folderId, newParentFolderId);

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 폴더 이동 실패 - folderId={}, newParentFolderId={}, error={}",
                folderId, newParentFolderId, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_UPDATE_ERROR);
        }
    }

    @Override
    public void uploadFile(String folderId, String driveFileName, InputStream content, long fileSize) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(driveFileName);
            fileMetadata.setParents(Collections.singletonList(folderId));

            InputStreamContent mediaContent = new InputStreamContent(
                "application/octet-stream", content
            );
            mediaContent.setLength(fileSize);

            drive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

            log.info("[GoogleDriveAdapter] Drive 파일 업로드 완료 - folderId={}, fileName={}",
                folderId, driveFileName);

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 파일 업로드 실패 - folderId={}, fileName={}, error={}",
                folderId, driveFileName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_UPLOAD_ERROR);
        }
    }

    @Override
    public boolean fileExists(String folderId, String fileName) {
        try {
            String query = String.format(
                "name='%s' and '%s' in parents and trashed=false",
                fileName, folderId
            );

            FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id)")
                .execute();

            List<File> files = result.getFiles();
            return files != null && !files.isEmpty();

        } catch (IOException e) {
            log.warn("[GoogleDriveAdapter] Drive 파일 존재 확인 실패 - folderId={}, fileName={}, error={}",
                folderId, fileName, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteFile(String folderId, String fileName) {
        try {
            String query = String.format(
                "name='%s' and '%s' in parents and trashed=false",
                fileName, folderId
            );

            FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id)")
                .execute();

            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                log.info("[GoogleDriveAdapter] Drive 파일 없음 (삭제 skip) - folderId={}, fileName={}",
                    folderId, fileName);
                return;
            }

            drive.files().delete(files.get(0).getId()).execute();
            log.info("[GoogleDriveAdapter] Drive 파일 삭제 완료 - folderId={}, fileName={}",
                folderId, fileName);

        } catch (IOException e) {
            log.error("[GoogleDriveAdapter] Drive 파일 삭제 실패 - folderId={}, fileName={}, error={}",
                folderId, fileName, e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_DRIVE_DELETE_ERROR);
        }
    }
}
