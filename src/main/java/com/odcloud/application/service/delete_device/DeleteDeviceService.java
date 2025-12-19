package com.odcloud.application.service.delete_device;

import com.odcloud.application.port.in.DeleteDeviceUseCase;
import com.odcloud.application.port.in.command.DeleteDeviceCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteDeviceService implements DeleteDeviceUseCase {

    private final AccountDeviceStoragePort storagePort;

    @Override
    @Transactional
    public DeleteDeviceServiceResponse delete(DeleteDeviceCommand command) {
        AccountDevice device = storagePort.findByAccountIdAndOsTypeAndDeviceId(
                command.accountId(),
                command.osType(),
                command.deviceId()
            )
            .orElseThrow(() -> new CustomBusinessException(
                ErrorCode.Business_DoesNotExists_DEVICE));

        if (!device.getAccountId().equals(command.accountId())) {
            throw new CustomBusinessException(ErrorCode.ACCESS_DENIED);
        }

        storagePort.delete(device.getId());
        return DeleteDeviceServiceResponse.ofSuccess();
    }
}
