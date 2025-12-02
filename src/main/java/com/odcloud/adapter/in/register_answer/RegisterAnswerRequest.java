package com.odcloud.adapter.in.register_answer;

import com.odcloud.application.port.in.command.RegisterAnswerCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterAnswerRequest(

    @NotBlank(message = "내용은 필수입니다")
    String content
) {

    RegisterAnswerCommand toCommand(Long questionId, Account account) {
        return RegisterAnswerCommand.builder()
            .questionId(questionId)
            .account(account)
            .content(content)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
