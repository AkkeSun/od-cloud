package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.register_notice.RegisterNoticeCommand;
import com.odcloud.application.group.service.register_notice.RegisterNoticeResponse;

public interface RegisterNoticeUseCase {

    RegisterNoticeResponse register(RegisterNoticeCommand command);
}
