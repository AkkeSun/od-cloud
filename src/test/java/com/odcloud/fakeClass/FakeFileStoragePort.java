package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileStoragePort implements FileInfoStoragePort {

    public List<FileInfo> database = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void save(FileInfo file) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        database.add(file);
        log.info("FakeFileStoragePort saved file: name={}", file.getFileName());
    }

    @Override
    public FileInfo findById(Long id) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(file -> file.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE));
    }

    @Override
    public List<FileInfo> findByIds(List<Long> ids) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        List<FileInfo> result = database.stream()
            .filter(file -> ids.contains(file.getId()))
            .toList();

        if (result.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE);
        }

        return result;
    }

    @Override
    public List<FileInfo> findAll(
        com.odcloud.application.port.in.command.FindFilesCommand command) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        // keyword 검색인 경우
        if (command.keyword() != null && !command.keyword().isBlank()) {
            return database.stream()
                .filter(file -> file.getFileName().contains(command.keyword()))
                .toList();
        }

        // folderId로 필터링
        return database.stream()
            .filter(file -> file.getFolderId().equals(command.folderId()))
            .toList();
    }

    @Override
    public boolean existsByFolderIdAndName(Long folderId, String name) {
        return database.stream()
            .anyMatch(file -> file.getFolderId().equals(folderId) && file.getFileName().equals(name));
    }

    @Override
    public List<FileInfo> findByFolderId(Long folderId) {
        return database.stream()
            .filter(file -> file.getFolderId().equals(folderId))
            .toList();
    }

    @Override
    public void delete(FileInfo file) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        database.remove(file);
        log.info("FakeFileStoragePort deleted file: id={}, name={}", file.getId(), file.getFileName());
    }

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
