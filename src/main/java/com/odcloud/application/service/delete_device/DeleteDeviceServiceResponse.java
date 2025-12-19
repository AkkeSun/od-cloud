package com.odcloud.application.service.delete_device;

public record DeleteDeviceServiceResponse(
    Boolean result
) {

    public static DeleteDeviceServiceResponse ofSuccess() {
        return new DeleteDeviceServiceResponse(Boolean.TRUE);
    }
}
