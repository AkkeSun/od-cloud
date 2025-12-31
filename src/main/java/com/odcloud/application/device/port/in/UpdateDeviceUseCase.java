package com.odcloud.application.device.port.in;

import com.odcloud.application.device.port.in.command.UpdateDeviceCommand;
import com.odcloud.application.device.service.update_device.UpdateDeviceServiceResponse;

public interface UpdateDeviceUseCase {

    UpdateDeviceServiceResponse update(UpdateDeviceCommand command);
}
