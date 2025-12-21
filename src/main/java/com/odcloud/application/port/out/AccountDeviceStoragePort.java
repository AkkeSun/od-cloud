package com.odcloud.application.port.out;

import com.odcloud.domain.model.AccountDevice;
import java.util.List;
import java.util.Optional;

public interface AccountDeviceStoragePort {

    AccountDevice save(AccountDevice device);

    Optional<AccountDevice> findByAccountIdAndOsTypeAndDeviceId(
        Long accountId, String osType, String deviceId
    );

    List<AccountDevice> findByGroupIdForPush(String groupId);

    void delete(Long id);

    void updateFcmToken(List<AccountDevice> invalidDevices);
}
