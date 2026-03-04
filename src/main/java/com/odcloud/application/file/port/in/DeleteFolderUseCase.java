package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.delete_folder.DeleteFolderResponse;
import com.odcloud.domain.model.Account;

public interface DeleteFolderUseCase {

    DeleteFolderResponse deleteFolder(Account account, Long folderId);
}
