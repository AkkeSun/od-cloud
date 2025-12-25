package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateGroupAccountUseYnCommand(
    String groupId,
    Account account,
    String showYn
) {

}
