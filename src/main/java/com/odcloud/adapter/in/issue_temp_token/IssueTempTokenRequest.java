package com.odcloud.adapter.in.issue_temp_token;

import com.odcloud.application.port.in.command.IssueTempTokenCommand;
import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record IssueTempTokenRequest(

    @NotBlank(message = "로그인 아이디는 필수값 입니다")
    String username,

    @NotBlank(message = "비밀번호는 필수값 입니다")
    String password
) {

    IssueTempTokenCommand toCommand() {
        return IssueTempTokenCommand.builder()
            .username(username)
            .password(password)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
