package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.delete_notice.DeleteNoticeServiceResponse;
import com.odcloud.application.port.in.command.DeleteNoticeCommand;

public interface DeleteNoticeUseCase {

    DeleteNoticeServiceResponse delete(DeleteNoticeCommand command);
}
