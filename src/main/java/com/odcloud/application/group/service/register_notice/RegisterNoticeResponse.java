package com.odcloud.application.group.service.register_notice;

public record RegisterNoticeResponse(
    Boolean result
) {

    public static RegisterNoticeResponse ofSuccess() {
        return new RegisterNoticeResponse(Boolean.TRUE);
    }
}
