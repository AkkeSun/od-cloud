package com.odcloud.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // code xx99 == filer level error

    // status code 401 (2001 - 2099) : Unauthorized
    INVALID_ACCESS_TOKEN(2001, "유효한 인증 토큰이 아닙니다"),
    INVALID_REFRESH_TOKEN(2002, "유효한 리프레시 토큰이 아닙니다"),
    INVALID_GOOGLE_TOKEN(2003, "유효한 구글 토큰이 아닙니다"),
    INVALID_ACCESS_TOKEN_BY_SECURITY(2099, "유효한 인증 토큰이 아닙니다"),

    // status code 403 (3001 - 3099) : Forbidden
    ACCESS_DENIED(3001, "접근권한이 없습니다"),
    ACCESS_DENIED_BY_SECURITY(3099, "접근권한이 없습니다"),

    // status code 500 (4001 - 4099) : Internal Server Error
    Business_SEND_EMAIL_ERROR(4001, "이메일 전송에 실패했습니다"),
    Business_SAVED_USER(4002, "등록된 사용자 정보 입니다"),
    Business_NOT_FOUND_ACCOUNT(4003, "조회된 사용자 정보가 없습니다"),
    Business_INVALID_PASSWORD(4004, "유효하지 않은 비밀번호 입니다"),
    Business_INVALID_OTP(4005, "유효하지 않은 OTP 입니다"),
    Business_ADMIN_NOT_APPROVED(4006, "관리자 승인이 필요합니다"),
    Business_SLACK_CLIENT_ERROR(4007, "슬랙 클라이언트 호출에 실패했습니다"),
    Business_APPROVE_USER(4008, "이미 관리자 승인이 완료된 사용자 입니다"),
    Business_SAVED_GROUP(4009, "등록된 그룹 정보 입니다"),

    ;


    private final int code;
    private final String message;
}