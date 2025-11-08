package com.odcloud.application.port.in;

import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;

public interface IssueTokenUseCase {

    IssueTokenServiceResponse issue(String googleAuthorization);
}
