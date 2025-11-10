package com.odcloud.adapter.in.create_file;

import com.odcloud.application.service.create_file.CreateFileServiceResponse;
import lombok.Builder;

@Builder
record CreateFileResponse(Boolean status) {

    static CreateFileResponse of(CreateFileServiceResponse response) {
        return CreateFileResponse.builder()
            .status(response.status())
            .build();
    }
}
