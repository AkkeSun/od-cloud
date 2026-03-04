package com.odcloud.application.question.service.register_answer;

import lombok.Builder;

@Builder
public record RegisterAnswerResponse(
    Boolean result
) {

    public static RegisterAnswerResponse ofSuccess() {
        return RegisterAnswerResponse.builder()
            .result(true)
            .build();
    }
}
