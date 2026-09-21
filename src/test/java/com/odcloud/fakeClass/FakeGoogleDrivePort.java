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
    public boolean shouldThrowRenameFolder = false;
    public boolean shouldThrowMoveFolder = false;
    public boolean shouldThrowFindFolder = false;
    public int ensureFolderCallCount = 0;
    public int ensureSubFolderCallCount = 0;
    public int uploadFileCallCount = 0;
    public int deleteFileCallCount = 0;
    public int fileExistsCallCount = 0;
    public int renameFolderCallCount = 0;
    public int moveFolderCallCount = 0;
    public int findFolderCallCount = 0;
    public List<String> uploadedFileNames = new ArrayList<>();
    public List<String> uploadedFolderIds = new ArrayList<>();
    public List<String> deletedFileNames = new ArrayList<>();
    public List<String> renamedFolderNames = new ArrayList<>();
    public List<String> movedFolderIds = new ArrayList<>();
    public List<String> movedToParentFolderIds = new ArrayList<>();
    // folderId → 파일명 집합: 테스트에서 사전 존재 파일을 설정할 때 사용
    public Map<String, Set<String>> preExistingFiles = new HashMap<>();
    // "parentFolderId::name" → driveFolderId: 테스트에서 이미 Drive에 존재하는 폴더를 설정할 때 사용
    public Map<String, String> existingFolders = new HashMap<>();

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
    public String findFolder(String parentFolderId, String folderName) {
        if (shouldThrowFindFolder) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR);
        }
        findFolderCallCount++;
        String driveFolderId = existingFolders.get(parentFolderId + "::" + folderName);
        log.info("FakeGoogleDrivePort findFolder: parentFolderId={}, folderName={}, found={}",
            parentFolderId, folderName, driveFolderId != null);
        return driveFolderId;
    }

    public void addExistingFolder(String parentFolderId, String folderName, String driveFolderId) {
        existingFolders.put(parentFolderId + "::" + folderName, driveFolderId);
    }

    @Override
    public void renameFolder(String folderId, String newName) {
        if (shouldThrowRenameFolder) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_UPDATE_ERROR);
        }
        renameFolderCallCount++;
        renamedFolderNames.add(newName);
        log.info("FakeGoogleDrivePort renameFolder: folderId={}, newName={}", folderId, newName);
    }

    @Override
    public void moveFolder(String folderId, String newParentFolderId) {
        if (shouldThrowMoveFolder) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_UPDATE_ERROR);
        }
        moveFolderCallCount++;
        movedFolderIds.add(folderId);
        movedToParentFolderIds.add(newParentFolderId);
        log.info("FakeGoogleDrivePort moveFolder: folderId={}, newParentFolderId={}", folderId, newParentFolderId);
    }

    @Override
    public synchronized void uploadFile(String folderId, String driveFileName, InputStream content, long fileSize) {
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
    public synchronized boolean fileExists(String folderId, String fileName) {
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
        shouldThrowRenameFolder = false;
        shouldThrowMoveFolder = false;
        shouldThrowFindFolder = false;
        ensureFolderCallCount = 0;
        ensureSubFolderCallCount = 0;
        uploadFileCallCount = 0;
        deleteFileCallCount = 0;
        fileExistsCallCount = 0;
        renameFolderCallCount = 0;
        moveFolderCallCount = 0;
        findFolderCallCount = 0;
        uploadedFileNames.clear();
        uploadedFolderIds.clear();
        deletedFileNames.clear();
        renamedFolderNames.clear();
        movedFolderIds.clear();
        movedToParentFolderIds.clear();
        preExistingFiles.clear();
        existingFolders.clear();
    }
}
