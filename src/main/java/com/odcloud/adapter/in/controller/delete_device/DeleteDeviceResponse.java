package com.odcloud.adapter.in.controller.delete_device;

import com.odcloud.application.service.delete_device.DeleteDeviceServiceResponse;

record DeleteDeviceResponse(
    Boolean result
) {

    static DeleteDeviceResponse of(DeleteDeviceServiceResponse serviceResponse) {
        return new DeleteDeviceResponse(serviceResponse.result());
    }
}
