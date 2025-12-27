package com.odcloud.application.service.update_notice;

public record UpdateNoticeServiceResponse(
    Boolean result
) {

    public static UpdateNoticeServiceResponse ofSuccess() {
        return new UpdateNoticeServiceResponse(Boolean.TRUE);
    }
}
