package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
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
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
