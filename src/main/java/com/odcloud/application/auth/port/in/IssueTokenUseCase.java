package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.issue_token.IssueTokenResponse;

public interface IssueTokenUseCase {

    IssueTokenResponse issue(String googleAuthorization, String deviceId);
}
