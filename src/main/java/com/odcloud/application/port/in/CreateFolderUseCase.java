package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.CreateFolderCommand;
import com.odcloud.application.service.create_folder.CreateFolderServiceResponse;

public interface CreateFolderUseCase {

    CreateFolderServiceResponse createFolder(CreateFolderCommand command);
}
