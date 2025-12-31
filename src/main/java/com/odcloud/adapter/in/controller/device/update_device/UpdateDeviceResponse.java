package com.odcloud.adapter.in.controller.device.update_device;

import com.odcloud.application.device.service.update_device.UpdateDeviceServiceResponse;

record UpdateDeviceResponse(
    Boolean result
) {

    static UpdateDeviceResponse of(UpdateDeviceServiceResponse serviceResponse) {
        return new UpdateDeviceResponse(serviceResponse.result());
    }
}
