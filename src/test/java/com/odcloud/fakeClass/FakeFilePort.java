package com.odcloud.fakeClass;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
public class FakeFilePort implements FilePort {

    public boolean shouldThrowException = false;
    public int createFolderCallCount = 0;
    public int uploadFileCallCount = 0;
    public int deleteFilesCallCount = 0;
    public int readFileCallCount = 0;
    public int readFilesCallCount = 0;
    public int moveFolderCallCount = 0;
    public int moveFileCallCount = 0;
    public List<String> deletedFiles = new ArrayList<>();
    public List<String> movedFiles = new ArrayList<>();
    public String lastMovedOldPath;
    public String lastMovedNewPath;

    @Override
    public void createFolder(String folderPath) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        createFolderCallCount++;
        log.info("FakeFilePort created folder: {}", folderPath);
    }

    @Override
    public void uploadFile(FileInfo file) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        uploadFileCallCount++;
        log.info("FakeFilePort uploaded file: {}", file.getFileName());
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        deleteFilesCallCount++;
        deletedFiles.addAll(filePaths);
        log.info("FakeFilePort deleted files: {}", filePaths);
    }

    @Override
    public void deleteFile(String filePath) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        deletedFiles.add(filePath);
        log.info("FakeFilePort deleted file: {}", filePath);
    }

    @Override
    public FileResponse readFile(FileInfo fileInfo) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        readFileCallCount++;

        // Create mock file content
        byte[] content = ("Mock content for " + fileInfo.getFileName()).getBytes();
        Resource resource = new ByteArrayResource(content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename(fileInfo.getFileName(), StandardCharsets.UTF_8)
            .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(content.length);

        return FileResponse.builder()
            .resource(resource)
            .headers(headers)
            .build();
    }

    @Override
    public FileResponse readFiles(List<FileInfo> files) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        readFilesCallCount++;

        // Create mock ZIP content
        byte[] zipContent = "Mock ZIP content".getBytes();
        Resource resource = new ByteArrayResource(zipContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename("files.zip", StandardCharsets.UTF_8)
            .build());

        return FileResponse.builder()
            .resource(resource)
            .headers(headers)
            .build();
    }

    @Override
    public void moveFolder(String oldPath, String newPath) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        moveFolderCallCount++;
        lastMovedOldPath = oldPath;
        lastMovedNewPath = newPath;
        log.info("FakeFilePort moved folder: {} -> {}", oldPath, newPath);
    }

    @Override
    public void deleteFolder(String folderPath) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        log.info("FakeFilePort deleted folder: {}", folderPath);
    }

    @Override
    public void moveFile(String oldPath, String newPath) {
        if (shouldThrowException) {
            throw new RuntimeException("File operation failure");
        }
        moveFileCallCount++;
        movedFiles.add(oldPath + " -> " + newPath);
        log.info("FakeFilePort moved file: {} -> {}", oldPath, newPath);
    }

    public void reset() {
        shouldThrowException = false;
        createFolderCallCount = 0;
        uploadFileCallCount = 0;
        deleteFilesCallCount = 0;
        readFileCallCount = 0;
        readFilesCallCount = 0;
        moveFolderCallCount = 0;
        moveFileCallCount = 0;
        deletedFiles.clear();
        movedFiles.clear();
        lastMovedOldPath = null;
        lastMovedNewPath = null;
    }
}
