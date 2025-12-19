package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAccountDeviceEntity.accountDeviceEntity;

import com.odcloud.domain.model.AccountDevice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AccountDeviceRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Transactional
    public AccountDevice save(AccountDevice device) {
        AccountDeviceEntity entity = toEntity(device);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    public Optional<AccountDevice> findByAccountIdAndOsTypeAndDeviceId(
        Long accountId, String osType, String deviceId
    ) {
        AccountDeviceEntity entity = queryFactory
            .selectFrom(accountDeviceEntity)
            .where(
                accountDeviceEntity.accountId.eq(accountId)
                    .and(accountDeviceEntity.osType.eq(osType))
                    .and(accountDeviceEntity.deviceId.eq(deviceId))
            )
            .fetchOne();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    private AccountDeviceEntity toEntity(AccountDevice device) {
        return AccountDeviceEntity.builder()
            .id(device.getId())
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
    }

    private AccountDevice toDomain(AccountDeviceEntity entity) {
        return AccountDevice.builder()
            .id(entity.getId())
            .accountId(entity.getAccountId())
            .osType(entity.getOsType())
            .deviceId(entity.getDeviceId())
            .appVersion(entity.getAppVersion())
            .fcmToken(entity.getFcmToken())
            .pushYn(entity.getPushYn())
            .lastLoginDt(entity.getLastLoginDt())
            .modDt(entity.getModDt())
            .regDt(entity.getRegDt())
            .build();
    }
}
