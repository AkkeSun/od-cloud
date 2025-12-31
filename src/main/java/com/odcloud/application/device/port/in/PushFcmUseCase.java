package com.odcloud.application.device.port.in;

import com.odcloud.application.device.port.in.command.PushFcmCommand;

public interface PushFcmUseCase {

    void push(PushFcmCommand command);

    void pushAsync(PushFcmCommand command);
}
