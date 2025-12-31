package com.odcloud.adapter.in.controller.group.join_group;

import com.odcloud.application.group.service.join_group.JoinGroupServiceResponse;

record JoinGroupResponse(
    Boolean result
) {

    static JoinGroupResponse of(JoinGroupServiceResponse serviceResponse) {
        return new JoinGroupResponse(serviceResponse.result());
    }
}
