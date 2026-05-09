package com.odcloud.application.device.service.register_device;

import com.odcloud.application.device.port.in.RegisterDeviceUseCase;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RegisterDeviceService implements RegisterDeviceUseCase {

    private final AccountDeviceStoragePort storagePort;

    @Override
    @Transactional
    public RegisterDeviceResponse register(RegisterDeviceCommand command) {
        Optional<AccountDevice> existingDevice = storagePort.findByAccountIdAndOsTypeAndDeviceId(
            command.accountId(),
            command.osType(),
            command.deviceId()
        );

        AccountDevice savedDevice;
        if (existingDevice.isPresent()) {
            AccountDevice device = existingDevice.get();
            device.updateDeviceInfo(command.fcmToken(), command.appVersion());
            savedDevice = storagePort.save(device);
        } else {
            savedDevice = storagePort.save(AccountDevice.builder()
                .accountId(command.accountId())
                .osType(command.osType())
                .deviceId(command.deviceId())
                .appVersion(command.appVersion())
                .fcmToken(command.fcmToken())
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build());
        }
        return RegisterDeviceResponse.of(savedDevice);
    }
}
