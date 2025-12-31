package com.odcloud.adapter.in.controller.group.update_notice;

import com.odcloud.application.group.service.update_notice.UpdateNoticeServiceResponse;

record UpdateNoticeResponse(
    Boolean result
) {

    static UpdateNoticeResponse of(UpdateNoticeServiceResponse serviceResponse) {
        return new UpdateNoticeResponse(serviceResponse.result());
    }
}
