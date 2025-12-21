package com.odcloud.adapter.in.controller.delete_file;

import com.odcloud.application.service.delete_file.DeleteFileServiceResponse;
import java.util.List;
import lombok.Builder;

@Builder
record DeleteFileResponse(
    Boolean result,
    List<DeleteFileResponseItem> logs
) {

    static DeleteFileResponse of(DeleteFileServiceResponse serviceResponse) {
        List<DeleteFileResponseItem> logs = serviceResponse.logs().stream()
            .map(item -> DeleteFileResponseItem.builder()
                .fileId(item.fileId())
                .errorMessage(item.errorMessage())
                .build())
            .toList();

        return DeleteFileResponse.builder()
            .result(serviceResponse.result())
            .logs(logs)
            .build();
    }
}
