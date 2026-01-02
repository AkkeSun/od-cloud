package com.odcloud.application.group.service.update_group;

import com.odcloud.domain.model.Group;

public record UpdateGroupServiceResponse(
    Boolean result,
    String ownerEmail,
    String name
) {

    public static UpdateGroupServiceResponse of(Group group) {
        return new UpdateGroupServiceResponse(
            Boolean.TRUE,
            group.getOwnerEmail(),
            group.getName()
        );
    }
}
