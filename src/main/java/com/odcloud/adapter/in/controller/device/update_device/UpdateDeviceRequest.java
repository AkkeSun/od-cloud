package com.odcloud.adapter.in.controller.device.update_device;

import com.odcloud.application.device.port.in.command.UpdateDeviceCommand;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateDeviceRequest(
    @NotBlank(message = "osType은 필수값 입니다")
    String osType,

    @NotBlank(message = "deviceId는 필수값 입니다")
    String deviceId,

    String pushYn,

    String fcmToken
) {

    UpdateDeviceCommand toCommand(Long accountId) {
        return UpdateDeviceCommand.builder()
            .accountId(accountId)
            .osType(osType)
            .deviceId(deviceId)
            .pushYn(pushYn)
            .fcmToken(fcmToken)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
