package com.odcloud.adapter.in.controller.file.update_file;

import com.odcloud.application.file.service.update_file.UpdateFileServiceResponse;
import lombok.Builder;

@Builder
record UpdateFileResponse(
    Boolean result
) {

    static UpdateFileResponse of(UpdateFileServiceResponse response) {
        return UpdateFileResponse.builder()
            .result(response.result())
            .build();
    }
}
