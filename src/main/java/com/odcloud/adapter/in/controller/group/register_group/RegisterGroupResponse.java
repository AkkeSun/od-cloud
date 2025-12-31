package com.odcloud.adapter.in.controller.group.register_group;

import com.odcloud.application.group.service.register_group.RegisterGroupServiceResponse;
import lombok.Builder;

@Builder
record RegisterGroupResponse(
    Boolean result
) {

    static RegisterGroupResponse of(RegisterGroupServiceResponse serviceResponse) {
        return RegisterGroupResponse.builder()
            .result(serviceResponse.result())
            .build();
    }
}
