package com.odcloud.application.service.register_notice;

public record RegisterNoticeServiceResponse(
    Boolean result
) {

    public static RegisterNoticeServiceResponse ofSuccess() {
        return new RegisterNoticeServiceResponse(Boolean.TRUE);
    }
}
