package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateDeviceCommand;
import com.odcloud.application.service.update_device.UpdateDeviceServiceResponse;

public interface UpdateDeviceUseCase {

    UpdateDeviceServiceResponse update(UpdateDeviceCommand command);
}
