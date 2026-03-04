package com.odcloud.application.device.port.in;

import com.odcloud.application.device.service.register_device.RegisterDeviceCommand;
import com.odcloud.application.device.service.register_device.RegisterDeviceResponse;

public interface RegisterDeviceUseCase {

    RegisterDeviceResponse register(RegisterDeviceCommand command);
}
