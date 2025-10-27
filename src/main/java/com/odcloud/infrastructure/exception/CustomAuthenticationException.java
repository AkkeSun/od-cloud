package com.odcloud.infrastructure.exception;

import lombok.Getter;

@Getter
public class CustomAuthenticationException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
