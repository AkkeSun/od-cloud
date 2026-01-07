package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAccountDeviceEntity.accountDeviceEntity;
import static com.odcloud.adapter.out.persistence.jpa.QAccountEntity.accountEntity;
import static com.odcloud.adapter.out.persistence.jpa.QGroupAccountEntity.groupAccountEntity;

import com.odcloud.domain.model.AccountDevice;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
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

    public List<AccountDevice> findByGroupIdForPush(Long groupId) {
        return queryFactory.select(Projections.constructor(AccountDevice.class,
                accountDeviceEntity.id,
                accountDeviceEntity.accountId,
                accountDeviceEntity.osType,
                accountDeviceEntity.deviceId,
                accountDeviceEntity.fcmToken,
                accountDeviceEntity.appVersion
            ))
            .from(accountDeviceEntity)
            .where(accountDeviceEntity.accountId.in(
                    queryFactory.select(groupAccountEntity.accountId)
                        .from(groupAccountEntity)
                        .where(groupAccountEntity.groupId.eq(groupId))
                        .fetch())
                .and(accountDeviceEntity.pushYn.eq("Y"))
                .and(accountDeviceEntity.fcmToken.ne("RESET")))
            .fetch();
    }

    public List<AccountDevice> findByAccountEmailForPush(String accountEmail) {
        return queryFactory.select(Projections.constructor(AccountDevice.class,
                accountDeviceEntity.id,
                accountDeviceEntity.accountId,
                accountDeviceEntity.osType,
                accountDeviceEntity.deviceId,
                accountDeviceEntity.fcmToken,
                accountDeviceEntity.appVersion
            ))
            .from(accountDeviceEntity)
            .innerJoin(accountEntity).on(accountDeviceEntity.accountId.eq(accountEntity.id))
            .where(accountEntity.email.eq(accountEmail)
                .and(accountDeviceEntity.pushYn.eq("Y"))
                .and(accountDeviceEntity.fcmToken.ne("RESET")))
            .fetch();
    }

    @Transactional
    public void updateFcmToken(List<AccountDevice> accountDevices) {
        queryFactory.update(accountDeviceEntity)
            .set(accountDeviceEntity.fcmToken, accountDevices.getFirst().getFcmToken())
            .set(accountDeviceEntity.modDt, accountDevices.getFirst().getModDt())
            .where(accountDeviceEntity.id.in(
                accountDevices.stream().map(AccountDevice::getId).toList()))
            .execute();
    }

    public List<AccountDevice> findByAccountId(Long accountId) {
        return queryFactory.select(Projections.constructor(AccountDevice.class,
                accountDeviceEntity.id,
                accountDeviceEntity.accountId,
                accountDeviceEntity.osType,
                accountDeviceEntity.deviceId,
                accountDeviceEntity.fcmToken,
                accountDeviceEntity.appVersion
            ))
            .from(accountDeviceEntity)
            .where(accountDeviceEntity.accountId.eq(accountId))
            .fetch();
    }

    @Transactional
    public void deleteByAccountId(Long accountId) {
        queryFactory.delete(accountDeviceEntity)
            .where(accountDeviceEntity.accountId.eq(accountId))
            .execute();
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
