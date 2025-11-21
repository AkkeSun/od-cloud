package com.odcloud.adapter.in.update_file;

import com.odcloud.application.port.in.command.UpdateFileCommand;
import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
record UpdateFileRequest(

    String fileName,

    Long folderId
) {

    UpdateFileCommand toCommand(Long fileId, Account account) {
        return UpdateFileCommand.builder()
            .fileId(fileId)
            .account(account)
            .fileName(fileName)
            .folderId(folderId)
            .build();
    }
}
