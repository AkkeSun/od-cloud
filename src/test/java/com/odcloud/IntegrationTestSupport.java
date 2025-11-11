package com.odcloud;

import com.odcloud.infrastructure.constant.ProfileConstant;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @Bean
    public ProfileConstant profileConstant() {
        ProfileConstant.Jwt jwt = new ProfileConstant.Jwt(
            300000L,
            1800000L,
            604800000L,
            "test-secret-key-for-jwt-must-be-long-enough-for-hmac-sha256"
        );

        ProfileConstant.GoogleOAuth2 googleOAuth2 = new ProfileConstant.GoogleOAuth2(
            "test-client-id",
            "test-client-secret",
            "http://localhost:8080/callback",
            "https://oauth2.googleapis.com/token",
            "https://www.googleapis.com/oauth2/v2/userinfo"
        );

        ProfileConstant.RedisKey redisKey = new ProfileConstant.RedisKey(
            "test-redis-key"
        );

        ProfileConstant.FileUpload fileUpload = new ProfileConstant.FileUpload(
            "/tmp/test-uploads"
        );

        return new ProfileConstant(
            jwt,
            googleOAuth2,
            redisKey,
            fileUpload,
            "test-aes-secret-key"
        );
    }
}