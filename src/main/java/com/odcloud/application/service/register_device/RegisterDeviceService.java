package com.odcloud.application.service.register_device;

import com.odcloud.application.port.in.RegisterDeviceUseCase;
import com.odcloud.application.port.in.command.RegisterDeviceCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
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
    public RegisterDeviceServiceResponse register(RegisterDeviceCommand command) {
        Optional<AccountDevice> existingDevice = storagePort.findByAccountIdAndOsTypeAndDeviceId(
            command.accountId(),
            command.osType(),
            command.deviceId()
        );

        if (existingDevice.isPresent()) {
            AccountDevice device = existingDevice.get();
            device.updateDeviceInfo(command);
            storagePort.save(device);
        } else {
            storagePort.save(AccountDevice.of(command));
        }
        return RegisterDeviceServiceResponse.ofSuccess();
    }
}
