package com.odcloud.application.group.service.delete_notice;

public record DeleteNoticeServiceResponse(
    Boolean result
) {

    public static DeleteNoticeServiceResponse ofSuccess() {
        return new DeleteNoticeServiceResponse(Boolean.TRUE);
    }
}
