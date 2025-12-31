package com.odcloud.adapter.in.controller.device.register_device;

import com.odcloud.application.device.service.register_device.RegisterDeviceServiceResponse;

record RegisterDeviceResponse(
    Boolean result
) {

    static RegisterDeviceResponse of(RegisterDeviceServiceResponse serviceResponse) {
        return new RegisterDeviceResponse(serviceResponse.result());
    }
}
