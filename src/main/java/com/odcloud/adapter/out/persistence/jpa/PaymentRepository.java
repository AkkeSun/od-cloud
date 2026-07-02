package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Payment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class PaymentRepository {

    private final EntityManager entityManager;

    @Transactional
    Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.of(payment);
        if (payment.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity.toDomain();
    }
}
