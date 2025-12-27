package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterNoticeCommand;
import com.odcloud.application.service.register_notice.RegisterNoticeServiceResponse;

public interface RegisterNoticeUseCase {

    RegisterNoticeServiceResponse register(RegisterNoticeCommand command);
}
