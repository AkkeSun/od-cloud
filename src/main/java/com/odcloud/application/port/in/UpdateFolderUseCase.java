package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateFolderCommand;
import com.odcloud.application.service.update_folder.UpdateFolderServiceResponse;

public interface UpdateFolderUseCase {

    UpdateFolderServiceResponse updateFolder(UpdateFolderCommand command);
}
