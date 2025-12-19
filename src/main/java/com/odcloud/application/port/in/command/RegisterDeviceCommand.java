package com.odcloud.application.port.in.command;

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
