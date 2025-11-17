package com.odcloud.fakeClass;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.domain.model.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileUploadPort implements FilePort {

    public List<String> createdFolders = new ArrayList<>();
    public List<File> uploadedFiles = new ArrayList<>();
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
    public void uploadFile(File file) {
        if (shouldThrowException) {
            throw new RuntimeException("File upload failure");
        }
        uploadedFiles.add(file);
        log.info("FakeFileUploadPort uploadFile: fileName={}", file.getFileName());
    }

    @Override
    public FileResponse readFile(File fileInfo) {
        return null;
    }

    @Override
    public FileResponse readFiles(List<File> files) {
        return null;
    }

    public void reset() {
        createdFolders.clear();
        uploadedFiles.clear();
        shouldThrowException = false;
    }
}
