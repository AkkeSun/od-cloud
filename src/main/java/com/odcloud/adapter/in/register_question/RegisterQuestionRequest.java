package com.odcloud.adapter.in.register_question;

import com.odcloud.application.port.in.command.RegisterQuestionCommand;
import com.odcloud.domain.model.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterQuestionRequest(

    @NotBlank(message = "제목은 필수입니다")
    String title,

    @NotBlank(message = "내용은 필수입니다")
    String content
) {

    RegisterQuestionCommand toCommand(Account account) {
        return RegisterQuestionCommand.builder()
            .account(account)
            .title(title)
            .content(content)
            .build();
    }
}
