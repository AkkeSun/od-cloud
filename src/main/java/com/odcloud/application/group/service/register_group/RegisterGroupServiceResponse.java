package com.odcloud.application.group.service.register_group;

public record RegisterGroupServiceResponse(
    Boolean result
) {

    public static RegisterGroupServiceResponse ofSuccess() {
        return new RegisterGroupServiceResponse(Boolean.TRUE);
    }
}
