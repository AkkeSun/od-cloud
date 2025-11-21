package com.odcloud.application.service.register_answer;

import lombok.Builder;

@Builder
public record RegisterAnswerServiceResponse(
    Boolean result
) {

    public static RegisterAnswerServiceResponse ofSuccess() {
        return RegisterAnswerServiceResponse.builder()
            .result(true)
            .build();
    }
}
