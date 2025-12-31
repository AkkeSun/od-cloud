package com.odcloud.adapter.in.controller.device.register_device;

import com.odcloud.application.device.port.in.command.RegisterDeviceCommand;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterDeviceRequest(
    @NotBlank(message = "osType은 필수값 입니다")
    String osType,

    @NotBlank(message = "deviceId는 필수값 입니다")
    String deviceId,

    @NotBlank(message = "appVersion은 필수값 입니다")
    String appVersion,

    @NotBlank(message = "fcmToken은 필수값 입니다")
    String fcmToken
) {

    RegisterDeviceCommand toCommand(Long accountId) {
        return RegisterDeviceCommand.builder()
            .accountId(accountId)
            .osType(osType)
            .deviceId(deviceId)
            .appVersion(appVersion)
            .fcmToken(fcmToken)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
