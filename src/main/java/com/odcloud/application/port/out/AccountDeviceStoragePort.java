package com.odcloud.application.port.out;

import com.odcloud.domain.model.AccountDevice;
import java.util.Optional;

public interface AccountDeviceStoragePort {

    AccountDevice save(AccountDevice device);

    Optional<AccountDevice> findByAccountIdAndOsTypeAndDeviceId(
        Long accountId, String osType, String deviceId
    );

    void delete(Long id);
}
