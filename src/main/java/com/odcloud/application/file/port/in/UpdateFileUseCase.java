package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.UpdateFileCommand;
import com.odcloud.application.file.service.update_file.UpdateFileServiceResponse;

public interface UpdateFileUseCase {

    UpdateFileServiceResponse update(UpdateFileCommand command);
}
