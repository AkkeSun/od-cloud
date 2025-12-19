package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record DeleteDeviceCommand(
    Long accountId,
    String osType,
    String deviceId
) {

}
