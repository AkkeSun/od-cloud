package com.odcloud.adapter.in.controller.register_question;

import com.odcloud.application.service.register_question.RegisterQuestionServiceResponse;
import lombok.Builder;

@Builder
public record RegisterQuestionResponse(
    Boolean result
) {

    public static RegisterQuestionResponse of(RegisterQuestionServiceResponse response) {
        return RegisterQuestionResponse.builder()
            .result(response.result())
            .build();
    }
}
