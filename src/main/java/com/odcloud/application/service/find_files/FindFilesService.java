package com.odcloud.application.service.find_files;

import com.odcloud.application.port.in.FindFilesUseCase;
import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindFilesService implements FindFilesUseCase {

    private final FileInfoStoragePort fileStoragePort;
    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindFilesServiceResponse findAll(FindFilesCommand command) {
        List<FileInfo> files = fileStoragePort.findAll(command);
        List<FolderInfo> folders = folderStoragePort.findAll(command);
        return FindFilesServiceResponse.of(files, folders, command.folderId());
    }
}
