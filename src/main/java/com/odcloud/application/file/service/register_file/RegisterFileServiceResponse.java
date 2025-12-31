package com.odcloud.application.file.service.register_file;

public record RegisterFileServiceResponse(
    Boolean result
) {

    public static RegisterFileServiceResponse ofSuccess() {
        return new RegisterFileServiceResponse(Boolean.TRUE);
    }
}
