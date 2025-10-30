package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.IssueTokenCommand;
import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;

public interface IssueTokenUseCase {

    IssueTokenServiceResponse issue(IssueTokenCommand command);
}
