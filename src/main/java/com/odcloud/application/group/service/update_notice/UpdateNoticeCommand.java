package com.odcloud.application.group.service.update_notice;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateNoticeCommand(
    Long groupId,
    Long noticeId,
    Account account,
    String title,
    String content
) {

}
