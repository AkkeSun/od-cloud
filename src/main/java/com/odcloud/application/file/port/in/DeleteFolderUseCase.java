package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.delete_folder.DeleteFolderServiceResponse;
import com.odcloud.domain.model.Account;

public interface DeleteFolderUseCase {

    DeleteFolderServiceResponse deleteFolder(Account account, Long folderId);
}
