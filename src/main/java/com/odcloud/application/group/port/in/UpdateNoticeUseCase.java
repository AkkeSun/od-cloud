package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_notice.UpdateNoticeCommand;
import com.odcloud.application.group.service.update_notice.UpdateNoticeResponse;

public interface UpdateNoticeUseCase {

    UpdateNoticeResponse update(UpdateNoticeCommand command);
}
