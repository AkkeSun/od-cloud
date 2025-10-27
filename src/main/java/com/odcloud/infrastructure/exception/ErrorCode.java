package com.odcloud.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // code xx99 == filer level error

    // status code 401 (2001 - 2099) : Unauthorized
    INVALID_ACCESS_TOKEN(2001, "유효한 인증 토큰이 아닙니다"),

    // status code 403 (3001 - 3099) : Forbidden
    ACCESS_DENIED(3001, "접근권한이 없습니다"),
    ACCESS_DENIED_BY_SECURITY(3099, "접근권한이 없습니다"),

    ;

    private final int code;
    private final String message;
}