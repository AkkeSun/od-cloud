package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.UpdateFolderCommand;
import com.odcloud.application.file.service.update_folder.UpdateFolderServiceResponse;

public interface UpdateFolderUseCase {

    UpdateFolderServiceResponse updateFolder(UpdateFolderCommand command);
}
