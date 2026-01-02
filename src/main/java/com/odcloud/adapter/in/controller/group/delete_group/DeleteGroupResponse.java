package com.odcloud.adapter.in.controller.group.delete_group;

import com.odcloud.application.group.service.delete_group.DeleteGroupServiceResponse;

record DeleteGroupResponse(
    Boolean result
) {

    static DeleteGroupResponse of(DeleteGroupServiceResponse response) {
        return new DeleteGroupResponse(response.result());
    }
}
