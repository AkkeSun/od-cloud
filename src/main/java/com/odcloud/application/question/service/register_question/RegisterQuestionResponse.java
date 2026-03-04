package com.odcloud.application.question.service.register_question;

import lombok.Builder;

@Builder
public record RegisterQuestionResponse(
    Boolean result
) {

    public static RegisterQuestionResponse ofSuccess() {
        return RegisterQuestionResponse.builder()
            .result(true)
            .build();
    }
}
