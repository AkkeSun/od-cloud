package com.odcloud.fakeClass;

import com.odcloud.infrastructure.constant.ProfileConstant;

public class FakeProfileConstant {

    public static ProfileConstant create() {
        return ProfileConstant.builder()
            .jwt(ProfileConstant.Jwt.builder()
                .tempTokenTtl(300000L)
                .accessTokenTtl(900000L)
                .refreshTokenTtl(604800000L)
                .secretKey("fake-secret-key-for-testing-purposes-minimum-256-bits")
                .build())
            .googleOAuth2(ProfileConstant.GoogleOAuth2.builder()
                .clientId("fake-client-id")
                .clientSecret("fake-client-secret")
                .redirectUri("http://localhost:8080/callback")
                .tokenApi("https://oauth2.googleapis.com/token")
                .userInfoApi("https://www.googleapis.com/oauth2/v2/userinfo")
                .build())
            .redisKey(ProfileConstant.RedisKey.builder()
                .token("token:%s:%s")
                .build())
            .fileUpload(ProfileConstant.FileUpload.builder()
                .basePath("/tmp/test-uploads")
                .build())
            .aesSecretKey("fake-aes-secret-key-for-testing")
            .build();
    }
}
