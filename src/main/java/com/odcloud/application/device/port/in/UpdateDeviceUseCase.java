package com.odcloud.application.device.port.in;

import com.odcloud.application.device.service.update_device.UpdateDeviceCommand;
import com.odcloud.application.device.service.update_device.UpdateDeviceResponse;

public interface UpdateDeviceUseCase {

    UpdateDeviceResponse update(UpdateDeviceCommand command);
}
