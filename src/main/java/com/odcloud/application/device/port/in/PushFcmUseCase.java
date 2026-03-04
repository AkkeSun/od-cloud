package com.odcloud.application.device.port.in;

import com.odcloud.application.device.service.push_fcm.PushFcmCommand;

public interface PushFcmUseCase {

    void push(PushFcmCommand command);

    void pushAsync(PushFcmCommand command);
}
