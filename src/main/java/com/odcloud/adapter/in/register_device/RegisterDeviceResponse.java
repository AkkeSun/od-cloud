package com.odcloud.adapter.in.register_device;

import com.odcloud.application.service.register_device.RegisterDeviceServiceResponse;

record RegisterDeviceResponse(
    Boolean result
) {

    static RegisterDeviceResponse of(RegisterDeviceServiceResponse serviceResponse) {
        return new RegisterDeviceResponse(serviceResponse.result());
    }
}
