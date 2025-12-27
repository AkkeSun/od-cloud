package com.odcloud.adapter.in.controller.register_notice;

import com.odcloud.application.service.register_notice.RegisterNoticeServiceResponse;

record RegisterNoticeResponse(
    Boolean result
) {

    static RegisterNoticeResponse of(RegisterNoticeServiceResponse response) {
        return new RegisterNoticeResponse(response.result());
    }
}
