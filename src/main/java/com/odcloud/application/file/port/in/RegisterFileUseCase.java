package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.RegisterFileCommand;
import com.odcloud.application.file.service.register_file.RegisterFileServiceResponse;

public interface RegisterFileUseCase {

    RegisterFileServiceResponse register(RegisterFileCommand command);
}
