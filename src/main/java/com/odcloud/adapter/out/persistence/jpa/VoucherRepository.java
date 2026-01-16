package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QVoucherEntity.voucherEntity;

import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
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
class VoucherRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Transactional
    public Voucher save(Voucher voucher) {
        VoucherEntity entity = toEntity(voucher);
        entityManager.persist(toEntity(voucher));
        return toDomain(entity);
    }

    @Transactional
    public void update(Voucher voucher) {
        queryFactory.update(voucherEntity)
            .set(voucherEntity.paymentId, voucher.getPaymentId())
            .set(voucherEntity.endDt, voucher.getEndDt())
            .set(voucherEntity.modDt, voucher.getModDt())
            .where(voucherEntity.id.eq(voucher.getId()))
            .execute();
    }

    public Optional<Voucher> findById(Long id) {
        VoucherEntity entity = queryFactory
            .selectFrom(voucherEntity)
            .where(voucherEntity.id.eq(id))
            .fetchOne();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    public Optional<Voucher> findForSubscription(
        Long groupId, VoucherType voucherType, Long accountId
    ) {
        VoucherEntity entity = queryFactory
            .selectFrom(voucherEntity)
            .where(
                voucherEntity.groupId.eq(groupId),
                voucherEntity.voucherType.eq(voucherType),
                voucherEntity.accountId.eq(accountId),
                voucherEntity.status.eq(VoucherStatus.ACTIVE)
            )
            .fetchOne();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    public List<Voucher> findActiveByAccountIdOrGroupIds(Long accountId, List<Long> groupIds) {
        return queryFactory
            .selectFrom(voucherEntity)
            .where(
                voucherEntity.status.eq(VoucherStatus.ACTIVE),
                voucherEntity.accountId.eq(accountId).or(voucherEntity.groupId.in(groupIds))
            )
            .fetch()
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private VoucherEntity toEntity(Voucher voucher) {
        return VoucherEntity.builder()
            .id(voucher.getId())
            .paymentId(voucher.getPaymentId())
            .voucherType(voucher.getVoucherType())
            .status(voucher.getStatus())
            .accountId(voucher.getAccountId())
            .groupId(voucher.getGroupId())
            .memo(voucher.getMemo())
            .startAt(voucher.getStartAt())
            .endDt(voucher.getEndDt())
            .modDt(voucher.getModDt())
            .regDt(voucher.getRegDt())
            .build();
    }

    private Voucher toDomain(VoucherEntity entity) {
        return Voucher.builder()
            .id(entity.getId())
            .paymentId(entity.getPaymentId())
            .voucherType(entity.getVoucherType())
            .status(entity.getStatus())
            .accountId(entity.getAccountId())
            .groupId(entity.getGroupId())
            .memo(entity.getMemo())
            .startAt(entity.getStartAt())
            .endDt(entity.getEndDt())
            .modDt(entity.getModDt())
            .regDt(entity.getRegDt())
            .build();
    }
}
