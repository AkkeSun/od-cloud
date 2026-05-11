package com.odcloud.adapter.out.client.google_drive;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_DELETE_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_DRIVE_UPLOAD_ERROR;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
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

    private final HttpTransport httpTransport;
    private final String shareEmail;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final Drive serviceAccountDrive;

    GoogleDriveAdapter(ProfileConstant profileConstant) throws IOException, GeneralSecurityException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.shareEmail = profileConstant.googleDrive().shareEmail();

        String userRefreshToken = profileConstant.googleDrive().userRefreshToken();
        if (userRefreshToken != null && !userRefreshToken.isBlank()) {
            this.clientId = profileConstant.googleDrive().clientId();
            this.clientSecret = profileConstant.googleDrive().clientSecret();
            this.refreshToken = userRefreshToken;
            this.serviceAccountDrive = null;
        } else {
            this.clientId = null;
            this.clientSecret = null;
            this.refreshToken = null;
            byte[] keyBytes = profileConstant.googleDrive().serviceAccountKeyJson()
                .getBytes(StandardCharsets.UTF_8);
            GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(keyBytes))
                .createScoped(Collections.singletonList(DriveScopes.DRIVE));
            this.serviceAccountDrive = new Drive.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
            ).setApplicationName(APPLICATION_NAME).build();
        }
    }

    /**
     * Refresh Token 방식: 호출마다 새로운 UserCredentials 생성 → Access Token 신규 발급
     * Service Account 방식: 기존 Drive 인스턴스 재사용
     */
    private Drive getDriveService() {
        if (refreshToken != null) {
            UserCredentials freshCredentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
            return new Drive.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(freshCredentials)
            ).setApplicationName(APPLICATION_NAME).build();
        }
        return serviceAccountDrive;
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

    @Override
    public String ensureFolder(String folderName) {
        Drive drive = getDriveService();
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
        Drive drive = getDriveService();
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
    public void uploadFile(String folderId, String driveFileName, InputStream content, long fileSize) {
        Drive drive = getDriveService();
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
        Drive drive = getDriveService();
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
        Drive drive = getDriveService();
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
