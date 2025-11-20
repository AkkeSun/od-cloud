package com.odcloud.fakeClass;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileUploadPort implements FilePort {

    public List<String> createdFolders = new ArrayList<>();
    public List<FileInfo> uploadedFiles = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void createFolder(String folderPath) {
        if (shouldThrowException) {
            throw new RuntimeException("Folder creation failure");
        }
        createdFolders.add(folderPath);
        log.info("FakeFileUploadPort createFolder: folderPath={}", folderPath);
    }

    @Override
    public void uploadFile(FileInfo file) {
        if (shouldThrowException) {
            throw new RuntimeException("File upload failure");
        }
        uploadedFiles.add(file);
        log.info("FakeFileUploadPort uploadFile: fileName={}", file.getFileName());
    }

    public List<String> deletedFiles = new ArrayList<>();

    @Override
    public void deleteFiles(List<String> filePaths) {
        if (shouldThrowException) {
            throw new RuntimeException("File deletion failure");
        }
        deletedFiles.addAll(filePaths);
        log.info("FakeFileUploadPort deleteFiles: filePaths={}", filePaths);
    }

    @Override
    public void deleteFile(String filePath) {
        if (shouldThrowException) {
            throw new RuntimeException("File deletion failure");
        }
        deletedFiles.add(filePath);
        log.info("FakeFileUploadPort deleteFile: filePath={}", filePath);
    }

    @Override
    public void deleteFolder(String folderPath) {
        if (shouldThrowException) {
            throw new RuntimeException("Folder deletion failure");
        }
        log.info("FakeFileUploadPort deleteFolder: folderPath={}", folderPath);
    }

    @Override
    public FileResponse readFile(FileInfo fileInfo) {
        return null;
    }

    @Override
    public FileResponse readFiles(List<FileInfo> files) {
        return null;
    }

    public void reset() {
        createdFolders.clear();
        uploadedFiles.clear();
        deletedFiles.clear();
        shouldThrowException = false;
    }
}
