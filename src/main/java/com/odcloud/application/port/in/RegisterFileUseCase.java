package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.application.service.register_file.RegisterFileServiceResponse;

public interface RegisterFileUseCase {

    RegisterFileServiceResponse register(RegisterFileCommand command);
}
