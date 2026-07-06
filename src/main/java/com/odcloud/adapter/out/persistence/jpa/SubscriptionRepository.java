package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;
import static com.odcloud.adapter.out.persistence.jpa.QProductEntity.productEntity;
import static com.odcloud.adapter.out.persistence.jpa.QSubscriptionEntity.subscriptionEntity;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Subscription;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
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

    SubscriptionEntity findById(Long subscriptionId) {
        return queryFactory.selectFrom(subscriptionEntity)
            .where(subscriptionEntity.id.eq(subscriptionId))
            .fetchOne();
    }

    List<Subscription> findByStatusAndNextBillingDateLoe(String status, LocalDate nextBillingDate) {
        return queryFactory.selectFrom(subscriptionEntity)
            .where(subscriptionEntity.status.eq(status)
                .and(subscriptionEntity.nextBillingDate.loe(nextBillingDate)))
            .fetch()
            .stream()
            .map(SubscriptionEntity::toDomain)
            .toList();
    }

    List<Subscription> findByStatusAndExpiredDateLoe(String status, LocalDate expiredDate) {
        return queryFactory.selectFrom(subscriptionEntity)
            .where(subscriptionEntity.status.eq(status)
                .and(subscriptionEntity.expiredDate.loe(expiredDate)))
            .fetch()
            .stream()
            .map(SubscriptionEntity::toDomain)
            .toList();
    }

    boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId) {
        return queryFactory.selectOne()
            .from(subscriptionEntity)
            .where(subscriptionEntity.groupId.eq(groupId)
                .and(subscriptionEntity.productId.eq(productId))
                .and(subscriptionEntity.status.in("ACTIVE")))
            .fetchFirst() != null;
    }

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        BooleanExpression groupMatch = groupIds.isEmpty()
            ? Expressions.FALSE
            : subscriptionEntity.groupId.in(groupIds);

        return queryFactory
            .select(Projections.constructor(
                SubscriptionDetail.class,
                productEntity.id,
                productEntity.productName,
                subscriptionEntity.id,
                groupEntity.id,
                groupEntity.name,
                buyerAccount.nickname,
                subscriptionEntity.status,
                subscriptionEntity.expiredDate
            ))
            .from(productEntity)
            .leftJoin(subscriptionEntity).on(subscriptionEntity.productId.eq(productEntity.id)
                .and(groupMatch)
                .and(subscriptionEntity.status.in("ACTIVE", "EXP_PENDING")))
            .leftJoin(groupEntity).on(subscriptionEntity.groupId.eq(groupEntity.id))
            .leftJoin(buyerAccount).on(subscriptionEntity.buyerId.eq(buyerAccount.id))
            .fetch();
    }
}
