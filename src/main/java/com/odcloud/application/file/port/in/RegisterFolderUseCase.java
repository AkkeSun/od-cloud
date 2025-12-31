package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.RegisterFolderCommand;
import com.odcloud.application.file.service.register_folder.RegisterFolderServiceResponse;

public interface RegisterFolderUseCase {

    RegisterFolderServiceResponse createFolder(RegisterFolderCommand command);
}
