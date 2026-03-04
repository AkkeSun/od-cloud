package com.odcloud.adapter.in.controller.group.delete_notice;

import com.odcloud.application.group.service.delete_notice.DeleteNoticeCommand;
import com.odcloud.domain.model.Account;

record DeleteNoticeRequest() {

    DeleteNoticeCommand toCommand(Long groupId, Long noticeId, Account account) {
        return DeleteNoticeCommand.builder()
            .groupId(groupId)
            .noticeId(noticeId)
            .account(account)
            .build();
    }
}
