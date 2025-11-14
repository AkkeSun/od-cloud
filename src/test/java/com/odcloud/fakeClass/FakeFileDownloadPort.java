package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FileDownloadPort;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFileDownloadPort implements FileDownloadPort {

    // fileLoc -> file content
    public Map<String, byte[]> fileStorage = new HashMap<>();
    public boolean shouldThrowException = false;

    @Override
    public InputStream readFile(String fileLoc) {
        if (shouldThrowException) {
            throw new RuntimeException("File read failure");
        }

        byte[] content = fileStorage.get(fileLoc);
        if (content == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileLoc);
        }

        log.info("FakeFileDownloadPort read file: fileLoc={}, size={}", fileLoc, content.length);
        return new ByteArrayInputStream(content);
    }

    @Override
    public boolean fileExists(String fileLoc) {
        if (shouldThrowException) {
            throw new RuntimeException("File existence check failure");
        }
        return fileStorage.containsKey(fileLoc);
    }

    public void addFile(String fileLoc, byte[] content) {
        fileStorage.put(fileLoc, content);
        log.info("FakeFileDownloadPort added file: fileLoc={}, size={}", fileLoc, content.length);
    }

    public void reset() {
        fileStorage.clear();
        shouldThrowException = false;
    }
}
