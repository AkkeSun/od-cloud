package com.odcloud.application.group.service.register_notice;

public record RegisterNoticeServiceResponse(
    Boolean result
) {

    public static RegisterNoticeServiceResponse ofSuccess() {
        return new RegisterNoticeServiceResponse(Boolean.TRUE);
    }
}
