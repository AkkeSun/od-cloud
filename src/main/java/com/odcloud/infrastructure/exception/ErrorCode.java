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
    Business_EMPTY_GROUP_ACCOUNT(4004, "승인된 그룹이 없는 사용자 입니다"),
    Business_SLACK_CLIENT_ERROR(4005, "슬랙 클라이언트 호출에 실패했습니다"),
    Business_APPROVE_USER(4006, "이미 관리자 승인이 완료된 사용자 입니다"),
    Business_SAVED_GROUP(4007, "등록된 그룹 정보 입니다"),
    Business_GOOGLE_USER_INFO_ERROR(4008, "구글 사용자 정보 조회중 오류가 발생했습니다"),
    Business_DoesNotExists_GROUP(4009, "등록되지 않은 그룹 입니다"),
    Business_DoesNotExists_GROUP_ACCOUNT(4010, "등록되지 않은 그룹 사용자 입니다"),
    Business_FILE_UPLOAD_ERROR(4011, "파일 업로드중 오류가 발생했습니다"),
    Business_DoesNotExists_FOLDER(4012, "조회된 폴더가 없습니다"),
    Business_SAVED_FOLDER_NAME(4013, "이미 사용중인 폴더명 입니다"),
    Business_INVALID_GROUP_OWNER(4014, "그룹 소유자만 처리할 수 있습니다"),
    Business_DoesNotExists_FILE(4015, "조회된 파일이 없습니다"),
    Business_FILE_DOWNLOAD_ERROR(4016, "파일 다운로드중 오류가 발생했습니다"),
    Business_SAVED_FILE_NAME(4017, "이미 사용중인 파일명 입니다"),
    Business_NOT_FOUND_SCHEDULE(4018, "조회된 스케줄이 없습니다"),
    Business_FORBIDDEN_ACCESS(4019, "접근 권한이 없습니다"),
    Business_NOT_FOUND_QUESTION(4020, "조회된 문의가 없습니다"),
    Business_ALREADY_EXISTS_ANSWER(4021, "이미 답변이 등록된 문의입니다"),
    Business_INVALID_GROUP_ACCOUNT_STATUS(4022, "PENDING 상태의 사용자만 상태 변경이 가능합니다"),
    Business_DoesNotExists_DEVICE(4023, "조회된 디바이스가 없습니다"),
    Business_NOT_FOUND_NOTICE(4024, "조회된 공지사항이 없습니다"),
    Business_GROUP_LIMIT_EXCEEDED(4025, "사용자당 최대 3개의 그룹만 생성할 수 있습니다"),
    Business_STORAGE_LIMIT_EXCEEDED(4026, "그룹 스토리지 용량이 부족합니다"),

    ;


    private final int code;
    private final String message;
}