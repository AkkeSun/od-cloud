package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QPaymentEntity.paymentEntity;

import com.odcloud.domain.model.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class PaymentRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Transactional
    public Payment save(Payment payment) {
        PaymentEntity entity = toEntity(payment);
        entityManager.persist(entity);
        return toDomain(entity);
    }

    @Transactional
    public void update(Payment payment) {
        queryFactory.update(paymentEntity)
            .set(paymentEntity.status, payment.getStatus())
            .where(paymentEntity.id.eq(payment.getId()))
            .execute();
    }

    public Optional<Payment> findById(Long id) {
        PaymentEntity entity = queryFactory
            .selectFrom(paymentEntity)
            .where(paymentEntity.id.eq(id))
            .fetchOne();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    public Optional<Payment> findBySubscriptionKey(String subscriptionKey) {
        PaymentEntity entity = queryFactory
            .selectFrom(paymentEntity)
            .where(paymentEntity.subscriptionKey.eq(subscriptionKey))
            .orderBy(paymentEntity.id.desc())
            .fetchFirst();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    public Optional<Payment> findByOrderTxId(String orderTxId) {
        PaymentEntity entity = queryFactory
            .selectFrom(paymentEntity)
            .where(paymentEntity.orderTxId.eq(orderTxId))
            .fetchOne();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    private PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
            .id(payment.getId())
            .accountId(payment.getAccountId())
            .storeType(payment.getStoreType())
            .subscriptionKey(payment.getSubscriptionKey())
            .status(payment.getStatus())
            .orderTxId(payment.getOrderTxId())
            .storeProcessDt(payment.getStoreProcessDt())
            .regDt(payment.getRegDt())
            .build();
    }

    private Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
            .id(entity.getId())
            .accountId(entity.getAccountId())
            .storeType(entity.getStoreType())
            .subscriptionKey(entity.getSubscriptionKey())
            .status(entity.getStatus())
            .orderTxId(entity.getOrderTxId())
            .storeProcessDt(entity.getStoreProcessDt())
            .regDt(entity.getRegDt())
            .build();
    }
}
