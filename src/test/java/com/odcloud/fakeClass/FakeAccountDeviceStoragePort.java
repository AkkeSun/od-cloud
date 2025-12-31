package com.odcloud.fakeClass;

import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.domain.model.AccountDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeAccountDeviceStoragePort implements AccountDeviceStoragePort {

    public List<AccountDevice> database = new ArrayList<>();
    public Long id = 0L;

    @Override
    public AccountDevice save(AccountDevice device) {
        AccountDevice savedDevice = AccountDevice.builder()
            .id(device.getId() == null ? ++id : device.getId())
            .accountId(device.getAccountId())
            .osType(device.getOsType())
            .deviceId(device.getDeviceId())
            .appVersion(device.getAppVersion())
            .fcmToken(device.getFcmToken())
            .pushYn(device.getPushYn())
            .lastLoginDt(device.getLastLoginDt())
            .modDt(device.getModDt())
            .regDt(device.getRegDt())
            .build();

        database.removeIf(d -> d.getId() != null && d.getId().equals(savedDevice.getId()));
        database.add(savedDevice);
        log.info("FakeAccountDeviceStoragePort saved: accountId={}, osType={}, deviceId={}",
            device.getAccountId(), device.getOsType(), device.getDeviceId());
        return savedDevice;
    }

    @Override
    public Optional<AccountDevice> findByAccountIdAndOsTypeAndDeviceId(
        Long accountId, String osType, String deviceId
    ) {
        Optional<AccountDevice> result = database.stream()
            .filter(d -> d.getAccountId().equals(accountId)
                && d.getOsType().equals(osType)
                && d.getDeviceId().equals(deviceId))
            .findFirst();
        log.info(
            "FakeAccountDeviceStoragePort findByAccountIdAndOsTypeAndDeviceId: accountId={}, osType={}, deviceId={}, found={}",
            accountId, osType, deviceId, result.isPresent());
        return result;
    }

    @Override
    public List<AccountDevice> findByGroupIdForPush(String groupId) {
        return List.of();
    }

    @Override
    public List<AccountDevice> findByAccountEmailForPush(String ownerEmail) {
        return List.of();
    }

    public void delete(Long id) {
        boolean removed = database.removeIf(d -> d.getId().equals(id));
        log.info("FakeAccountDeviceStoragePort delete: id={}, removed={}", id, removed);
    }

    @Override
    public void updateFcmToken(List<AccountDevice> invalidDevices) {
        for (AccountDevice invalidDevice : invalidDevices) {
            database.stream()
                .filter(d -> d.getId().equals(invalidDevice.getId()))
                .forEach(AccountDevice::resetFcmToken);
        }
        log.info("FakeAccountDeviceStoragePort updateFcmToken: count={}", invalidDevices.size());
    }
}
