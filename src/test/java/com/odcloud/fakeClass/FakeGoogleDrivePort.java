package com.odcloud.fakeClass;

import com.odcloud.application.file.port.out.GoogleDrivePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeGoogleDrivePort implements GoogleDrivePort {

    public static final String FIXED_FOLDER_ID = "fake-drive-folder-id";

    public boolean shouldThrowEnsureFolder = false;
    public boolean shouldThrowEnsureSubFolder = false;
    public boolean shouldThrowUploadFile = false;
    public boolean shouldThrowDeleteFile = false;
    public int ensureFolderCallCount = 0;
    public int ensureSubFolderCallCount = 0;
    public int uploadFileCallCount = 0;
    public int deleteFileCallCount = 0;
    public int fileExistsCallCount = 0;
    public List<String> uploadedFileNames = new ArrayList<>();
    public List<String> uploadedFolderIds = new ArrayList<>();
    public List<String> deletedFileNames = new ArrayList<>();
    // folderId → 파일명 집합: 테스트에서 사전 존재 파일을 설정할 때 사용
    public Map<String, Set<String>> preExistingFiles = new HashMap<>();

    @Override
    public String ensureFolder(String folderName) {
        if (shouldThrowEnsureFolder) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
        ensureFolderCallCount++;
        log.info("FakeGoogleDrivePort ensureFolder: folderName={}", folderName);
        return FIXED_FOLDER_ID;
    }

    @Override
    public String ensureSubFolder(String parentFolderId, String folderName) {
        if (shouldThrowEnsureSubFolder) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
        ensureSubFolderCallCount++;
        log.info("FakeGoogleDrivePort ensureSubFolder: parentFolderId={}, folderName={}", parentFolderId, folderName);
        return "fake-sub-folder-id-" + folderName;
    }

    @Override
    public void uploadFile(String folderId, String driveFileName, InputStream content, long fileSize) {
        if (shouldThrowUploadFile) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_UPLOAD_ERROR);
        }
        uploadFileCallCount++;
        uploadedFileNames.add(driveFileName);
        uploadedFolderIds.add(folderId);
        log.info("FakeGoogleDrivePort uploadFile: folderId={}, fileName={}", folderId, driveFileName);
    }

    @Override
    public void deleteFile(String folderId, String fileName) {
        if (shouldThrowDeleteFile) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_DELETE_ERROR);
        }
        deleteFileCallCount++;
        deletedFileNames.add(fileName);
        log.info("FakeGoogleDrivePort deleteFile: folderId={}, fileName={}", folderId, fileName);
    }

    @Override
    public boolean fileExists(String folderId, String fileName) {
        fileExistsCallCount++;
        Set<String> existingNames = preExistingFiles.getOrDefault(folderId, Set.of());
        boolean exists = existingNames.contains(fileName);
        log.info("FakeGoogleDrivePort fileExists: folderId={}, fileName={}, exists={}", folderId, fileName, exists);
        return exists;
    }

    public void addPreExistingFile(String folderId, String fileName) {
        preExistingFiles.computeIfAbsent(folderId, k -> new HashSet<>()).add(fileName);
    }

    public void reset() {
        shouldThrowEnsureFolder = false;
        shouldThrowEnsureSubFolder = false;
        shouldThrowUploadFile = false;
        shouldThrowDeleteFile = false;
        ensureFolderCallCount = 0;
        ensureSubFolderCallCount = 0;
        uploadFileCallCount = 0;
        deleteFileCallCount = 0;
        fileExistsCallCount = 0;
        uploadedFileNames.clear();
        uploadedFolderIds.clear();
        deletedFileNames.clear();
        preExistingFiles.clear();
    }
}
