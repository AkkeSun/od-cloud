package com.odcloud.application.file.port.in;

import com.odcloud.application.file.port.in.command.DeleteFileCommand;
import com.odcloud.application.file.service.delete_file.DeleteFileServiceResponse;

public interface DeleteFileUseCase {

    DeleteFileServiceResponse deleteFile(DeleteFileCommand command);
}
