package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import java.util.List;
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

    @Override
    public File findById(Long id) {
        return fileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public List<File> findByIds(List<Long> ids) {
        return fileRepository.findByIds(ids);
    }

    @Override
    public List<File> findByFolderId(Long folderId) {
        return fileRepository.findByFolderId(folderId);
    }
}
