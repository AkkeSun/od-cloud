package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DeleteFileCommand;
import com.odcloud.application.service.delete_file.DeleteFileServiceResponse;

public interface DeleteFileUseCase {

    DeleteFileServiceResponse deleteFile(DeleteFileCommand command);
}
