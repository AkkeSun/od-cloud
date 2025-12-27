package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateNoticeCommand;
import com.odcloud.application.service.update_notice.UpdateNoticeServiceResponse;

public interface UpdateNoticeUseCase {

    UpdateNoticeServiceResponse update(UpdateNoticeCommand command);
}
