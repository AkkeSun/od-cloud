package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record RegisterAnswerCommand(
    Long questionId,
    Account account,
    String content
) {

}
