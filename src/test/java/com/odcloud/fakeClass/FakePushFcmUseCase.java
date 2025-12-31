package com.odcloud.fakeClass;

import com.odcloud.application.device.port.in.command.PushFcmCommand;
import com.odcloud.application.device.port.in.PushFcmUseCase;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakePushFcmUseCase implements PushFcmUseCase {

    public List<PushFcmCommand> sentCommands = new ArrayList<>();

    @Override
    public void push(PushFcmCommand command) {
        sentCommands.add(command);
        log.info("FakePushFcmUseCase push: command={}", command);
    }

    @Override
    public void pushAsync(PushFcmCommand command) {
        sentCommands.add(command);
        log.info("FakePushFcmUseCase pushAsync: command={}", command);
    }
}
