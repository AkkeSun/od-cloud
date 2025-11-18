package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.application.service.find_files.FindFilesServiceResponse;

public interface FindFilesUseCase {

    FindFilesServiceResponse findAll(FindFilesCommand command);
}
