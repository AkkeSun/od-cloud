package com.odcloud.application.device.service.register_device;

public record RegisterDeviceServiceResponse(
    Boolean result
) {

    public static RegisterDeviceServiceResponse ofSuccess() {
        return new RegisterDeviceServiceResponse(Boolean.TRUE);
    }
}
