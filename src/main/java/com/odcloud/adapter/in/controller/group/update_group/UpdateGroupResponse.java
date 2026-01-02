package com.odcloud.adapter.in.controller.group.update_group;

import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;
import lombok.Builder;

@Builder
record UpdateGroupResponse(
    Boolean result,
    String ownerEmail,
    String name
) {

    static UpdateGroupResponse of(UpdateGroupServiceResponse serviceResponse) {
        return UpdateGroupResponse.builder()
            .result(serviceResponse.result())
            .ownerEmail(serviceResponse.ownerEmail())
            .name(serviceResponse.name())
            .build();
    }
}
