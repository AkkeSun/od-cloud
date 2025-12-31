package com.odcloud.application.device.port.in;

import com.odcloud.application.device.port.in.command.RegisterDeviceCommand;
import com.odcloud.application.device.service.register_device.RegisterDeviceServiceResponse;

public interface RegisterDeviceUseCase {

    RegisterDeviceServiceResponse register(RegisterDeviceCommand command);
}
