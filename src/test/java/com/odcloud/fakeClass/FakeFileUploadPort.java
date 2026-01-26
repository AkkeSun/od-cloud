package com.odcloud.fakeClass;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileUploadPort implements FilePort {

    public List<FileInfo> uploadedFiles = new ArrayList<>();
    public List<String> deletedFiles = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void uploadFile(FileInfo file) {
        if (shouldThrowException) {
            throw new RuntimeException("File upload failure");
        }
        uploadedFiles.add(file);
        log.info("FakeFileUploadPort uploadFile: fileName={}", file.getFileName());
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
    public FileResponse readFile(FileInfo fileInfo) {
        return null;
    }

    @Override
    public FileResponse readFiles(List<FileInfo> files) {
        return null;
    }

    public void reset() {
        uploadedFiles.clear();
        deletedFiles.clear();
        shouldThrowException = false;
    }
}
