package com.odcloud.fakeClass;

import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.application.auth.port.out.GoogleUserInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeGoogleOAuth2Port implements GoogleOAuth2Port {

    public boolean shouldThrowExceptionOnGetToken = false;
    public boolean shouldThrowExceptionOnGetUserInfo = false;
    public String mockAccessToken = null;
    public GoogleUserInfo mockUserInfoResponse = null;

    @Override
    public String getToken(String code) {
        log.info("FakeGoogleOAuth2Port getToken: code={}", code);

        if (shouldThrowExceptionOnGetToken) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        if (mockAccessToken != null) {
            return mockAccessToken;
        }

        return "fake-access-token";
    }

    @Override
    public GoogleUserInfo getUserInfo(String googleAccessToken) {
        log.info("FakeGoogleOAuth2Port getUserInfo: googleAccessToken={}", googleAccessToken);

        if (shouldThrowExceptionOnGetUserInfo) {
            throw new CustomBusinessException(ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        if (mockUserInfoResponse != null) {
            return mockUserInfoResponse;
        }

        return new GoogleUserInfo("fake@example.com", "가짜 사용자", "https://fake.example.com/photo.jpg");
    }

    public void reset() {
        shouldThrowExceptionOnGetToken = false;
        shouldThrowExceptionOnGetUserInfo = false;
        mockAccessToken = null;
        mockUserInfoResponse = null;
    }
}