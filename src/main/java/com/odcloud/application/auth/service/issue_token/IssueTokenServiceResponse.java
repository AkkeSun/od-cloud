package com.odcloud.application.auth.service.issue_token;

import lombok.Builder;

@Builder
public record IssueTokenServiceResponse(
    String accessToken,
    String refreshToken
) {

}
