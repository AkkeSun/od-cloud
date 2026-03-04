package com.odcloud.application.device.service.update_device;

import lombok.Builder;

@Builder
public record UpdateDeviceCommand(
    Long accountId,
    String osType,
    String deviceId,
    String pushYn,
    String fcmToken
) {

}