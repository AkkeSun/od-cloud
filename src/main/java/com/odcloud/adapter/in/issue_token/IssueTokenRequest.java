package com.odcloud.adapter.in.issue_token;

import com.odcloud.application.port.in.command.IssueTokenCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record IssueTokenRequest(
    @NotBlank(message = "OTP 는 필수값 입니다")
    String otp
) {

    IssueTokenCommand toCommand(Account account) {
        return IssueTokenCommand.builder()
            .opt(otp)
            .username(account.getUsername())
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
