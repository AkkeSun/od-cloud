package com.odcloud.application.group.service.join_group;

public record JoinGroupResponse(
    Boolean result
) {

    public static JoinGroupResponse ofSuccess() {
        return new JoinGroupResponse(Boolean.TRUE);
    }
}
