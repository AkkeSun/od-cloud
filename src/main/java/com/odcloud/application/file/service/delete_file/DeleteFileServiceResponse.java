package com.odcloud.application.file.service.delete_file;

import java.util.List;
import lombok.Builder;

@Builder
public record DeleteFileServiceResponse(
    Boolean result,
    List<DeleteFileServiceResponseItem> logs
) {

}
