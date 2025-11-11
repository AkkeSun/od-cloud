package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.application.service.register_folder.RegisterFolderServiceResponse;

public interface RegisterFolderUseCase {

    RegisterFolderServiceResponse createFolder(RegisterFolderCommand command);
}
