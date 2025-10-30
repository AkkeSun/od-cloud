package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.IssueTempTokenCommand;
import com.odcloud.application.service.issue_temp_token.IssueTempTokenServiceResponse;

public interface IssueTempTokenUseCase {

    IssueTempTokenServiceResponse issue(IssueTempTokenCommand command);
}
