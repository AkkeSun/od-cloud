package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.PushFcmCommand;

public interface PushFcmUseCase {

    void push(PushFcmCommand command);

    void pushAsync(PushFcmCommand command);
}
