package com.odcloud.application.service.update_device;

import com.odcloud.application.port.in.UpdateDeviceUseCase;
import com.odcloud.application.port.in.command.UpdateDeviceCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateDeviceService implements UpdateDeviceUseCase {

    private final AccountDeviceStoragePort storagePort;

    @Override
    @Transactional
    public UpdateDeviceServiceResponse update(UpdateDeviceCommand command) {
        AccountDevice device = storagePort.findByAccountIdAndOsTypeAndDeviceId(
            command.accountId(),
            command.osType(),
            command.deviceId()
        ).orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_DEVICE));

        device.updateDevice(command.pushYn(), command.fcmToken());
        storagePort.save(device);

        return UpdateDeviceServiceResponse.ofSuccess();
    }
}
