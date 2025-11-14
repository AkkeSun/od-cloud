package com.odcloud.infrastructure.constant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
@ConfigurationProperties("service-constant")
public record ProfileConstant(
    Jwt jwt,
    GoogleOAuth2 googleOAuth2,
    RedisKey redisKey,
    FileUpload fileUpload,
    @NotBlank
    String aesSecretKey
) {

    @Builder
    public record Jwt(
        @NotNull
        Long tempTokenTtl,
        @NotNull
        Long accessTokenTtl,
        @NotNull
        Long refreshTokenTtl,
        @NotBlank
        String secretKey
    ) {

    }

    @Builder
    public record GoogleOAuth2(
        @NotBlank
        String clientId,
        @NotBlank
        String clientSecret,
        @NotBlank
        String redirectUri,
        @NotBlank
        String tokenApi,
        @NotBlank
        String userInfoApi
    ) {

    }

    @Builder
    public record RedisKey(
        @NotBlank
        String token
    ) {

    }

    @Builder
    public record FileUpload(
        @NotBlank
        String basePath
    ) {

    }

    public Long getRefreshTokenTtl() {
        return jwt().refreshTokenTtl;
    }

    public Long getTempTokenTtl() {
        return jwt().tempTokenTtl;
    }

    public Long getAccessTokenTtl() {
        return jwt().accessTokenTtl;
    }

    public String getJwtSecretKey() {
        return jwt().secretKey;
    }

}
