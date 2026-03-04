package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.register_folder.RegisterFolderCommand;
import com.odcloud.application.file.service.register_folder.RegisterFolderResponse;

public interface RegisterFolderUseCase {

    RegisterFolderResponse createFolder(RegisterFolderCommand command);
}
