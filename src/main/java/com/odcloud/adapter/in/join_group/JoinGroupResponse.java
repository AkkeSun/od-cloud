package com.odcloud.adapter.in.join_group;

import com.odcloud.application.service.join_group.JoinGroupServiceResponse;

record JoinGroupResponse(
    Boolean result
) {

    static JoinGroupResponse of(JoinGroupServiceResponse serviceResponse) {
        return new JoinGroupResponse(serviceResponse.result());
    }
}
