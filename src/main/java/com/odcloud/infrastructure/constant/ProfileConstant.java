package com.odcloud.infrastructure.constant;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("service-constant")
public record ProfileConstant(
    @NotBlank
    String jasyptPassword
) {

}
