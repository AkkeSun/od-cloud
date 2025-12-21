package com.odcloud.adapter.in.controller.register_file;

import com.odcloud.application.service.register_file.RegisterFileServiceResponse;

record RegisterFileResponse(
    Boolean result
) {

    static RegisterFileResponse of(RegisterFileServiceResponse response) {
        return new RegisterFileResponse(response.result());
    }
}
