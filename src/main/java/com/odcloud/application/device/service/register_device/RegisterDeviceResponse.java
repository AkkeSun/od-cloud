package com.odcloud.application.device.service.register_device;

import com.odcloud.domain.model.AccountDevice;

public record RegisterDeviceResponse(
    Boolean result,
    Long accountId,
    String osType,
    String deviceId,
    String appVersion,
    String fcmToken,
    String pushYn
) {

    public static RegisterDeviceResponse of(AccountDevice device) {
        return new RegisterDeviceResponse(
            Boolean.TRUE,
            device.getAccountId(),
            device.getOsType(),
            device.getDeviceId(),
            device.getAppVersion(),
            device.getFcmToken(),
            device.getPushYn()
        );
    }
}
