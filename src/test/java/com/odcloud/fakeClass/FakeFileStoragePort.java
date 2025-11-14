package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileStoragePort implements FileStoragePort {

    public List<File> database = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void save(File file) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        // Simulate auto-increment ID
        if (file.getId() == null) {
            Long newId = database.stream()
                .mapToLong(File::getId)
                .max()
                .orElse(0L) + 1;
            file = File.builder()
                .id(newId)
                .folderId(file.getFolderId())
                .fileName(file.getFileName())
                .fileLoc(file.getFileLoc())
                .multipartFile(file.getMultipartFile())
                .modDt(file.getModDt())
                .regDt(file.getRegDt())
                .build();
        }
        database.add(file);
        log.info("FakeFileStoragePort saved file: id={}, fileName={}", file.getId(),
            file.getFileName());
    }

    @Override
    public File findById(Long id) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(file -> file.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public List<File> findByIds(List<Long> ids) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(file -> ids.contains(file.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<File> findByFolderId(Long folderId) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(file -> file.getFolderId().equals(folderId))
            .collect(Collectors.toList());
    }

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
