package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
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

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
