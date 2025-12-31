package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_notice.UpdateNoticeServiceResponse;
import com.odcloud.application.port.in.command.UpdateNoticeCommand;

public interface UpdateNoticeUseCase {

    UpdateNoticeServiceResponse update(UpdateNoticeCommand command);
}
