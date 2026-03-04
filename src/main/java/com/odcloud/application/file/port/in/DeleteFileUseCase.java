package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.delete_file.DeleteFileCommand;
import com.odcloud.application.file.service.delete_file.DeleteFileResponse;

public interface DeleteFileUseCase {

    DeleteFileResponse deleteFile(DeleteFileCommand command);
}
