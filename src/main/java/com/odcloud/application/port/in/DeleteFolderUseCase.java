package com.odcloud.application.port.in;

import com.odcloud.application.service.delete_folder.DeleteFolderServiceResponse;
import com.odcloud.domain.model.Account;

public interface DeleteFolderUseCase {

    DeleteFolderServiceResponse deleteFolder(Account account, Long folderId);
}
