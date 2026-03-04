package com.odcloud.application.device.service.update_device;

public record UpdateDeviceResponse(
    Boolean result
) {

    public static UpdateDeviceResponse ofSuccess() {
        return new UpdateDeviceResponse(Boolean.TRUE);
    }
}
