package com.odcloud.fakeClass;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeGoogleOAuth2Port implements GoogleOAuth2Port {

    public boolean shouldThrowExceptionOnGetToken = false;
    public boolean shouldThrowExceptionOnGetUserInfo = false;
    public GoogleTokenResponse mockTokenResponse = null;
    public GoogleUserInfoResponse mockUserInfoResponse = null;

    @Override
    public GoogleTokenResponse getToken(String code) {
        log.info("FakeGoogleOAuth2Port getToken: code={}", code);

        if (shouldThrowExceptionOnGetToken) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        if (mockTokenResponse != null) {
            return mockTokenResponse;
        }

        return new GoogleTokenResponse(
            "fake-access-token",
            "fake-id-token",
            "fake-refresh-token",
            "openid profile email",
            "Bearer",
            3600L
        );
    }

    @Override
    public GoogleUserInfoResponse getUserInfo(String googleAccessToken) {
        log.info("FakeGoogleOAuth2Port getUserInfo: googleAccessToken={}", googleAccessToken);

        if (shouldThrowExceptionOnGetUserInfo) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        if (mockUserInfoResponse != null) {
            return mockUserInfoResponse;
        }

        return GoogleUserInfoResponse.builder()
            .sub("fake-sub-123")
            .name("가짜 사용자")
            .given_name("사용자")
            .picture("https://fake.example.com/photo.jpg")
            .email("fake@example.com")
            .email_verified(true)
            .build();
    }

    public void reset() {
        shouldThrowExceptionOnGetToken = false;
        shouldThrowExceptionOnGetUserInfo = false;
        mockTokenResponse = null;
        mockUserInfoResponse = null;
    }
}