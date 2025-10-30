package com.odcloud.adapter.in.register_account;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.infrastructure.util.ToStringUtil;
import com.odcloud.infrastructure.validation.Contains;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.SizeGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
record RegisterAccountRequest(

    @NotBlank(message = "접속 계정은 필수값 입니다", groups = NotBlankGroups.class)
    @Size(max = 30, message = "접속 계정은 30자 이하로 입력 가능 합니다", groups = SizeGroups.class)
    String username,

    @NotBlank(message = "비밀번호는 필수값 입니다", groups = NotBlankGroups.class)
    String password,

    @NotBlank(message = "이메일은 필수값 입니다", groups = NotBlankGroups.class)
    @Size(max = 30, message = "이메일은 30자 이하로 입력 가능 합니다", groups = SizeGroups.class)
    String email,

    @Contains(values = {"ROLE_ADMIN", "ROLE_USER"},
        message = "유효하지 않은 권한 입니다", groups = CustomGroups.class)
    @NotEmpty(message = "권한은 필수값 입니다", groups = NotBlankGroups.class)
    String role
) {

    RegisterAccountCommand toCommand() {
        return RegisterAccountCommand.builder()
            .username(username)
            .password(password)
            .email(email)
            .role(role)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
