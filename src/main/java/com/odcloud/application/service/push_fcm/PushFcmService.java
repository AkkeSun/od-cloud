package com.odcloud.application.service.push_fcm;

import com.odcloud.application.port.in.PushFcmUseCase;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.application.port.out.FcmPort;
import com.odcloud.domain.model.AccountDevice;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class PushFcmService implements PushFcmUseCase {

    private final FcmPort fcmPort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;

    @Async
    @Override
    public void push(PushFcmCommand command) {
        List<AccountDevice> invalidDevices = fcmPort.push(command);
        if (!invalidDevices.isEmpty()) {
            invalidDevices.forEach(AccountDevice::resetFcmToken);
            accountDeviceStoragePort.updateFcmToken(invalidDevices);
        }
    }
}
