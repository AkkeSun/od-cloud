package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;
import static com.odcloud.adapter.out.persistence.jpa.QVoucherEntity.voucherEntity;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class VoucherRepository {

    private static final QAccountEntity payerAccount = new QAccountEntity("payerAccount");

    private final JPAQueryFactory queryFactory;

    List<VoucherDetail> findActiveByGroupIds(List<Long> groupIds) {
        return queryFactory
            .select(Projections.constructor(
                VoucherDetail.class,
                groupEntity.name,
                voucherEntity.voucherType,
                payerAccount.nickname,
                voucherEntity.endDt
            ))
            .from(voucherEntity)
            .innerJoin(groupEntity).on(voucherEntity.groupId.eq(groupEntity.id))
            .innerJoin(payerAccount).on(voucherEntity.paymentId.eq(payerAccount.id))
            .where(voucherEntity.groupId.in(groupIds)
                .and(voucherEntity.status.eq("ACTIVE")))
            .fetch();
    }
}
