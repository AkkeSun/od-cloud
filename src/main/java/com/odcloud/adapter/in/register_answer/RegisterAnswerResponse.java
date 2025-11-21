package com.odcloud.adapter.in.register_answer;

import com.odcloud.application.service.register_answer.RegisterAnswerServiceResponse;
import lombok.Builder;

@Builder
public record RegisterAnswerResponse(
    Boolean result
) {

    public static RegisterAnswerResponse of(RegisterAnswerServiceResponse response) {
        return RegisterAnswerResponse.builder()
            .result(response.result())
            .build();
    }
}
