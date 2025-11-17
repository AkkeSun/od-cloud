package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
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
        database.add(file);
        log.info("FakeFileStoragePort saved file: name={}", file.getFileName());
    }

    @Override
    public File findById(Long id) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(file -> file.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE));
    }

    @Override
    public List<File> findByIds(List<Long> ids) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        List<File> result = database.stream()
            .filter(file -> ids.contains(file.getId()))
            .toList();

        if (result.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE);
        }

        return result;
    }

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
