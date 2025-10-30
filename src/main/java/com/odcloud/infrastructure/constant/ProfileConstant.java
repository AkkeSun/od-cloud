package com.odcloud.infrastructure.constant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("service-constant")
public record ProfileConstant(
    Jwt jwt
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

}
