package com.odcloud.infrastructure.constant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("service-constant")
public record ProfileConstant(
    Jwt jwt,
    RedisKey redisKey,
    @NotBlank
    String aesSecretKey
) {

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

    public record RedisKey(
        @NotBlank
        String token
    ) {

    }

    public String getTokenRedisKey() {
        return redisKey.token;
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
