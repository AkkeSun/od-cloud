package com.odcloud.application.group.service.update_group;

public record UpdateGroupServiceResponse(
    Boolean result
) {

    public static UpdateGroupServiceResponse ofSuccess() {
        return new UpdateGroupServiceResponse(true);
    }
}
