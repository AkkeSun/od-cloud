package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.find_files.FindFilesCommand;
import com.odcloud.application.file.service.find_files.FindFilesResponse;

public interface FindFilesUseCase {

    FindFilesResponse findAll(FindFilesCommand command);
}
