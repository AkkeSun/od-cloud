package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;
import static com.odcloud.adapter.out.persistence.jpa.QProductEntity.productEntity;
import static com.odcloud.adapter.out.persistence.jpa.QSubscriptionEntity.subscriptionEntity;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class SubscriptionRepository {

    private static final QAccountEntity buyerAccount = new QAccountEntity("buyerAccount");

    private final JPAQueryFactory queryFactory;

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        return queryFactory
            .select(Projections.constructor(
                SubscriptionDetail.class,
                groupEntity.id,
                groupEntity.name,
                productEntity.productName,
                buyerAccount.id,
                buyerAccount.nickname,
                subscriptionEntity.status
            ))
            .from(productEntity)
            .leftJoin(subscriptionEntity).on(subscriptionEntity.productId.eq(productEntity.id)
                .and(subscriptionEntity.groupId.in(groupIds))
                .and(subscriptionEntity.status.in("ACTIVE", "EXP_PENDING")))
            .leftJoin(groupEntity).on(subscriptionEntity.groupId.eq(groupEntity.id))
            .leftJoin(buyerAccount).on(subscriptionEntity.buyerId.eq(buyerAccount.id))
            .fetch();
    }
}
