package com.odcloud.application.question.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record RegisterQuestionCommand(
    Account account,
    String title,
    String content
) {

}
