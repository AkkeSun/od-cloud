package com.odcloud.application.group.service.delete_group;

public record DeleteGroupResponse(
    Boolean result
) {

    public static DeleteGroupResponse ofSuccess() {
        return new DeleteGroupResponse(Boolean.TRUE);
    }
}
