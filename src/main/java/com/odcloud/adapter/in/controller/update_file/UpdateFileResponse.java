package com.odcloud.adapter.in.controller.update_file;

import com.odcloud.application.service.update_file.UpdateFileServiceResponse;
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
