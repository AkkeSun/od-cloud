package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterDeviceCommand;
import com.odcloud.application.service.register_device.RegisterDeviceServiceResponse;

public interface RegisterDeviceUseCase {

    RegisterDeviceServiceResponse register(RegisterDeviceCommand command);
}
