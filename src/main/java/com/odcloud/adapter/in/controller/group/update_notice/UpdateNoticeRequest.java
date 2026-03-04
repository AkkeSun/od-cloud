package com.odcloud.adapter.in.controller.group.update_notice;

import com.odcloud.application.group.service.update_notice.UpdateNoticeCommand;
import com.odcloud.domain.model.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateNoticeRequest(
    @NotBlank(message = "제목은 필수값 입니다")
    String title,
    @NotBlank(message = "내용은 필수값 입니다")
    String content
) {

    UpdateNoticeCommand toCommand(Long groupId, Long noticeId, Account account) {
        return UpdateNoticeCommand.builder()
            .groupId(groupId)
            .noticeId(noticeId)
            .account(account)
            .title(title)
            .content(content)
            .build();
    }
}
