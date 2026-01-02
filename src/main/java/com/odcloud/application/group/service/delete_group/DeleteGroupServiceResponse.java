package com.odcloud.application.group.service.delete_group;

public record DeleteGroupServiceResponse(
    Boolean result
) {

    public static DeleteGroupServiceResponse ofSuccess() {
        return new DeleteGroupServiceResponse(Boolean.TRUE);
    }
}
