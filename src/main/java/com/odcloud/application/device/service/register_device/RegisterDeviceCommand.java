package com.odcloud.application.device.service.register_device;

import lombok.Builder;

@Builder
public record RegisterDeviceCommand(
    Long accountId,
    String osType,
    String deviceId,
    String appVersion,
    String fcmToken
) {

}
