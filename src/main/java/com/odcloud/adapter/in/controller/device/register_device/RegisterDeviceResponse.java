package com.odcloud.adapter.in.controller.device.register_device;

import com.odcloud.application.device.service.register_device.RegisterDeviceServiceResponse;

record RegisterDeviceResponse(
    Boolean result,
    Long accountId,
    String osType,
    String deviceId,
    String appVersion,
    String fcmToken,
    String pushYn
) {

    static RegisterDeviceResponse of(RegisterDeviceServiceResponse serviceResponse) {
        return new RegisterDeviceResponse(
            serviceResponse.result(),
            serviceResponse.accountId(),
            serviceResponse.osType(),
            serviceResponse.deviceId(),
            serviceResponse.appVersion(),
            serviceResponse.fcmToken(),
            serviceResponse.pushYn()
        );
    }
}
