package com.odcloud.application.group.service.register_group;

public record RegisterGroupResponse(
    Boolean result
) {

    public static RegisterGroupResponse ofSuccess() {
        return new RegisterGroupResponse(Boolean.TRUE);
    }
}
