package com.odcloud.application.service.find_files;

import com.odcloud.application.port.in.FindFilesUseCase;
import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindFilesService implements FindFilesUseCase {

    private final FileStoragePort fileStoragePort;
    private final FolderStoragePort folderStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindFilesServiceResponse findAll(FindFilesCommand command) {
        List<File> files = fileStoragePort.findAll(command);
        List<Folder> folders = folderStoragePort.findAll(command);
        return FindFilesServiceResponse.of(files, folders, command.folderId());
    }
}
