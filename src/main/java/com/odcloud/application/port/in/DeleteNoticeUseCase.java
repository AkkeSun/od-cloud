package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DeleteNoticeCommand;
import com.odcloud.application.service.delete_notice.DeleteNoticeServiceResponse;

public interface DeleteNoticeUseCase {

    DeleteNoticeServiceResponse delete(DeleteNoticeCommand command);
}
