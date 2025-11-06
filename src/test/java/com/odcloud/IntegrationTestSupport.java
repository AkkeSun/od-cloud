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

        ProfileConstant.Slack slack = new ProfileConstant.Slack(
            "http://localhost:8080",
            "test-token-12345"
        );

        ProfileConstant.GoogleOAuth2 googleOAuth2 = new ProfileConstant.GoogleOAuth2(
            "test-client-id",
            "test-client-secret",
            "http://localhost:8080/callback"
        );

        return new ProfileConstant(
            jwt,
            slack,
            googleOAuth2,
            "test-redis-key",
            "test-aes-secret-key"
        );
    }
}