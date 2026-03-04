package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.update_file.UpdateFileCommand;
import com.odcloud.application.file.service.update_file.UpdateFileResponse;

public interface UpdateFileUseCase {

    UpdateFileResponse update(UpdateFileCommand command);
}
