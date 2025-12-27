package com.odcloud.adapter.in.controller.delete_notice;

import com.odcloud.application.service.delete_notice.DeleteNoticeServiceResponse;

record DeleteNoticeResponse(
    Boolean result
) {

    static DeleteNoticeResponse of(DeleteNoticeServiceResponse serviceResponse) {
        return new DeleteNoticeResponse(serviceResponse.result());
    }
}
