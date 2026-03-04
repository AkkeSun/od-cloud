package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.register_file.RegisterFileCommand;
import com.odcloud.application.file.service.register_file.RegisterFileResponse;

public interface RegisterFileUseCase {

    RegisterFileResponse register(RegisterFileCommand command);
}
