package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record RegisterNoticeCommand(
    Long groupId,
    Account account,
    String title,
    String content
) {

}
