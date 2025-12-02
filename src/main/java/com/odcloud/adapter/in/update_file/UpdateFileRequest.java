package com.odcloud.adapter.in.update_file;

import com.odcloud.application.port.in.command.UpdateFileCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
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

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
