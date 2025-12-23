package com.odcloud.application.port.in.command;

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