package com.odcloud.application.service.create_file;

import lombok.Builder;

@Builder
public record CreateFileServiceResponse(Boolean status) {

    public static CreateFileServiceResponse ofSuccess() {
        return CreateFileServiceResponse.builder().status(true).build();
    }
}
