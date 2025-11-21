package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateFileCommand;
import com.odcloud.application.service.update_file.UpdateFileServiceResponse;

public interface UpdateFileUseCase {

    UpdateFileServiceResponse update(UpdateFileCommand command);
}
