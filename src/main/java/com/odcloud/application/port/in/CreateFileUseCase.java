package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.CreateFileCommand;
import com.odcloud.application.service.create_file.CreateFileServiceResponse;

public interface CreateFileUseCase {

    CreateFileServiceResponse createFile(CreateFileCommand command);
}
