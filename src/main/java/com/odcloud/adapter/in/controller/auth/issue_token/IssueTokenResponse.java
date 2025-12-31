package com.odcloud.adapter.in.controller.auth.issue_token;

import com.odcloud.application.auth.service.issue_token.IssueTokenServiceResponse;
import lombok.Builder;

@Builder
record IssueTokenResponse(
    String accessToken,
    String refreshToken
) {

    static IssueTokenResponse of(IssueTokenServiceResponse serviceResponse) {
        return IssueTokenResponse.builder()
            .accessToken(serviceResponse.accessToken())
            .refreshToken(serviceResponse.refreshToken())
            .build();
    }
}
