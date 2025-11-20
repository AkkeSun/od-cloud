package com.odcloud.application.service.update_file;

public record UpdateFileServiceResponse(
    Boolean result
) {

    public static UpdateFileServiceResponse ofSuccess() {
        return new UpdateFileServiceResponse(Boolean.TRUE);
    }
}
