package com.odcloud.application.service.register_question;

import lombok.Builder;

@Builder
public record RegisterQuestionServiceResponse(
    Boolean result
) {

    public static RegisterQuestionServiceResponse ofSuccess() {
        return RegisterQuestionServiceResponse.builder()
            .result(true)
            .build();
    }
}
