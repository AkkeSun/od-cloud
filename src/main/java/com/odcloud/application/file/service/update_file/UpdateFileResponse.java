package com.odcloud.application.file.service.update_file;

public record UpdateFileResponse(
    Boolean result
) {

    public static UpdateFileResponse ofSuccess() {
        return new UpdateFileResponse(Boolean.TRUE);
    }
}
