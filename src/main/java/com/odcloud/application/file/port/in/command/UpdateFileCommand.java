package com.odcloud.application.file.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateFileCommand(

    Long fileId,

    Account account,

    String fileName,

    Long folderId
) {

}
