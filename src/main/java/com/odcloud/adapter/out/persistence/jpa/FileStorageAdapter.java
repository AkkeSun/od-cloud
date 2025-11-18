package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FILE;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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
        return fileRepository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_FILE));
    }

    @Override
    public List<File> findByIds(List<Long> ids) {
        return fileRepository.findByIds(ids);
    }

    @Override
    public List<File> findAll(FindFilesCommand command) {
        return fileRepository.findAll(command);
    }

    @Override
    public boolean existsByFolderIdAndName(Long folderId, String name) {
        return fileRepository.existsByFolderIdAndName(folderId, name);
    }
}
