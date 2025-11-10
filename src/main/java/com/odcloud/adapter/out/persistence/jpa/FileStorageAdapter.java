package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FileStorageAdapter implements FileStoragePort {

    private final FileRepository fileRepository;

    @Override
    public void save(File file) {
        fileRepository.save(file);
    }
}
