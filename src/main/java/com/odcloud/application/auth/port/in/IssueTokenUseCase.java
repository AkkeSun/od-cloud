package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.issue_token.IssueTokenServiceResponse;

public interface IssueTokenUseCase {

    IssueTokenServiceResponse issue(String googleAuthorization);
}
