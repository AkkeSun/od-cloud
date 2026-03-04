package com.odcloud.application.group.service.delete_notice;

public record DeleteNoticeResponse(
    Boolean result
) {

    public static DeleteNoticeResponse ofSuccess() {
        return new DeleteNoticeResponse(Boolean.TRUE);
    }
}
