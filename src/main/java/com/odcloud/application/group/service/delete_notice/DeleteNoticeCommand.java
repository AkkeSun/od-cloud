package com.odcloud.application.group.service.delete_notice;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record DeleteNoticeCommand(
    Long groupId,
    Long noticeId,
    Account account
) {

}
