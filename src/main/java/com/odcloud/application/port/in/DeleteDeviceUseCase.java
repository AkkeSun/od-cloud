package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DeleteDeviceCommand;
import com.odcloud.application.service.delete_device.DeleteDeviceServiceResponse;

public interface DeleteDeviceUseCase {

    DeleteDeviceServiceResponse delete(DeleteDeviceCommand command);
}
