package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.delete_notice.DeleteNoticeCommand;
import com.odcloud.application.group.service.delete_notice.DeleteNoticeResponse;

public interface DeleteNoticeUseCase {

    DeleteNoticeResponse delete(DeleteNoticeCommand command);
}
