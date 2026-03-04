package com.odcloud.application.file.service.register_file;

public record RegisterFileResponse(
    Boolean result
) {

    public static RegisterFileResponse ofSuccess() {
        return new RegisterFileResponse(Boolean.TRUE);
    }
}
