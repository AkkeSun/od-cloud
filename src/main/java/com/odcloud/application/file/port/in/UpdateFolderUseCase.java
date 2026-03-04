package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.update_folder.UpdateFolderCommand;
import com.odcloud.application.file.service.update_folder.UpdateFolderResponse;

public interface UpdateFolderUseCase {

    UpdateFolderResponse updateFolder(UpdateFolderCommand command);
}
