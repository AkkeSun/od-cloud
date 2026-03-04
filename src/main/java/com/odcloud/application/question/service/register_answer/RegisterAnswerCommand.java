package com.odcloud.application.question.service.register_answer;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record RegisterAnswerCommand(
    Long questionId,
    Account account,
    String content
) {

}
