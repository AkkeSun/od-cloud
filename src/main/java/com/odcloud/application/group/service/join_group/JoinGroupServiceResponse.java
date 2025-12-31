package com.odcloud.application.group.service.join_group;

public record JoinGroupServiceResponse(
    Boolean result
) {

    public static JoinGroupServiceResponse ofSuccess() {
        return new JoinGroupServiceResponse(Boolean.TRUE);
    }
}
