package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import java.util.List;
import lombok.Builder;

@Builder
public record DeleteFileCommand(
    Account account,
    List<Long> fileIds
) {
}
