package com.odcloud.application.device.service.update_device;

public record UpdateDeviceServiceResponse(
    Boolean result
) {

    public static UpdateDeviceServiceResponse ofSuccess() {
        return new UpdateDeviceServiceResponse(Boolean.TRUE);
    }
}
