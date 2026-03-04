package com.odcloud.application.group.service.update_notice;

public record UpdateNoticeResponse(
    Boolean result
) {

    public static UpdateNoticeResponse ofSuccess() {
        return new UpdateNoticeResponse(Boolean.TRUE);
    }
}
