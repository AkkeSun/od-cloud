package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.register_notice.RegisterNoticeServiceResponse;
import com.odcloud.application.port.in.command.RegisterNoticeCommand;

public interface RegisterNoticeUseCase {

    RegisterNoticeServiceResponse register(RegisterNoticeCommand command);
}
