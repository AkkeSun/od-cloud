package com.odcloud.adapter.in.controller.group.register_notice;

import com.odcloud.application.port.in.command.RegisterNoticeCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import org.springframework.util.StringUtils;

@Builder
record RegisterNoticeRequest(

    String title,

    @NotBlank(message = "내용은 필수값 입니다")
    String content
) {

    RegisterNoticeCommand toCommand(String groupId, Account account) {
        return RegisterNoticeCommand.builder()
            .groupId(groupId)
            .account(account)
            .title(StringUtils.hasText(title) ? title
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("M월 d일 공지사항")))
            .content(content)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
