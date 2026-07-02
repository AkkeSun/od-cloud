package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;
import static com.odcloud.adapter.out.persistence.jpa.QProductEntity.productEntity;
import static com.odcloud.adapter.out.persistence.jpa.QSubscriptionEntity.subscriptionEntity;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Subscription;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Transactional
    Subscription save(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionEntity.of(subscription);
        if (subscription.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity.toDomain();
    }

    boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId) {
        return queryFactory.selectOne()
            .from(subscriptionEntity)
            .where(subscriptionEntity.groupId.eq(groupId)
                .and(subscriptionEntity.productId.eq(productId))
                .and(subscriptionEntity.status.in("ACTIVE", "EXP_PENDING")))
            .fetchFirst() != null;
    }

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
