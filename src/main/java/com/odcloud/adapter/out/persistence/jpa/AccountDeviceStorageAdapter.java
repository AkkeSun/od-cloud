package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AccountDeviceStorageAdapter implements AccountDeviceStoragePort {

    private final AccountDeviceRepository repository;

    @Override
    public AccountDevice save(AccountDevice device) {
        return repository.save(device);
    }

    @Override
    public Optional<AccountDevice> findByAccountIdAndOsTypeAndDeviceId(
        Long accountId, String osType, String deviceId
    ) {
        return repository.findByAccountIdAndOsTypeAndDeviceId(accountId, osType, deviceId);
    }

    @Override
    public List<AccountDevice> findByGroupIdForPush(String groupId) {
        return repository.findByGroupIdForPush(groupId);
    }

    @Override
    public List<AccountDevice> findByWriterEmailForPush(String writerEmail) {
        return repository.findByWriterEmailForPush(writerEmail);
    }

    @Override
    public void updateFcmToken(List<AccountDevice> accountDevices) {
        repository.updateFcmToken(accountDevices);
    }
}
