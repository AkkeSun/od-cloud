package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FILE;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FileInfoStorageAdapter implements FileInfoStoragePort {

    private final FileInfoRepository fileRepository;

    @Override
    public void save(FileInfo file) {
        fileRepository.save(file);
    }

    @Override
    public FileInfo findById(Long id) {
        return fileRepository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_FILE));
    }

    @Override
    public List<FileInfo> findByIds(List<Long> ids) {
        return fileRepository.findByIds(ids);
    }

    @Override
    public List<FileInfo> findAll(FindFilesCommand command) {
        return fileRepository.findAll(command);
    }

    @Override
    public boolean existsByFolderIdAndName(Long folderId, String name) {
        return fileRepository.existsByFolderIdAndName(folderId, name);
    }

    @Override
    public List<FileInfo> findByFolderId(Long folderId) {
        return fileRepository.findByFolderId(folderId);
    }

    @Override
    public void delete(FileInfo file) {
        fileRepository.delete(file);
    }
}
