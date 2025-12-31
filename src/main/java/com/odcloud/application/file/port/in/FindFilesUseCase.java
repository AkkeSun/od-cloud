package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.application.file.service.find_files.FindFilesServiceResponse;

public interface FindFilesUseCase {

    FindFilesServiceResponse findAll(FindFilesCommand command);
}
