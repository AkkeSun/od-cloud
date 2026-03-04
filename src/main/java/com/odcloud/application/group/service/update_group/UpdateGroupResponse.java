package com.odcloud.application.group.service.update_group;

public record UpdateGroupResponse(
    Boolean result
) {

    public static UpdateGroupResponse ofSuccess() {
        return new UpdateGroupResponse(true);
    }
}
