package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Payment;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PaymentRepositoryTest extends IntegrationTestSupport {

    @Autowired
    PaymentRepository repository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM PaymentEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 결제 정보를 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 정리된 컬럼 기준으로 신규 결제를 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Payment payment = Payment.builder()
                .subscriptionId(1L)
                .buyerId(10L)
                .amount(new BigDecimal("9900"))
                .status("PAID")
                .regDt(now)
                .build();

            // when
            Payment result = repository.save(payment);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getSubscriptionId()).isEqualTo(1L);
            assertThat(result.getBuyerId()).isEqualTo(10L);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("9900"));
            assertThat(result.getStatus()).isEqualTo("PAID");

            PaymentEntity savedEntity = entityManager.find(PaymentEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getSubscriptionId()).isEqualTo(1L);
            assertThat(savedEntity.getBuyerId()).isEqualTo(10L);
            assertThat(savedEntity.getAmount()).isEqualByComparingTo(new BigDecimal("9900"));
            assertThat(savedEntity.getStatus()).isEqualTo("PAID");
        }

        @Test
        @DisplayName("[success] 기존 결제를 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            PaymentEntity existing = PaymentEntity.builder()
                .subscriptionId(1L)
                .buyerId(10L)
                .amount(new BigDecimal("9900"))
                .status("PAID")
                .regDt(now)
                .build();
            entityManager.persist(existing);
            entityManager.flush();
            entityManager.clear();

            Payment updated = Payment.builder()
                .id(existing.getId())
                .subscriptionId(1L)
                .buyerId(10L)
                .amount(new BigDecimal("9900"))
                .status("CANCELED")
                .regDt(now)
                .modDt(now.plusHours(1))
                .build();

            // when
            Payment result = repository.save(updated);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(existing.getId());
            assertThat(result.getStatus()).isEqualTo("CANCELED");

            PaymentEntity savedEntity = entityManager.find(PaymentEntity.class, existing.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getStatus()).isEqualTo("CANCELED");
        }
    }
}
