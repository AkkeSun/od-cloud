package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.PaymentStatus;
import com.odcloud.domain.model.StoreType;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PaymentStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    PaymentStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM PaymentEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 결제를 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 APPLE 결제를 저장한다")
        void success_applePayment() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Payment payment = Payment.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_apple_123")
                .orderTxId("ORDER_TX_12345")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            // when
            Payment result = adapter.save(payment);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getAccountId()).isEqualTo(1L);
            assertThat(result.getStoreType()).isEqualTo(StoreType.APPLE);
            assertThat(result.getOrderTxId()).isEqualTo("ORDER_TX_12345");
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
            assertThat(result.getStoreProcessDt()).isNotNull();

            PaymentEntity savedEntity = entityManager.find(PaymentEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getStoreType()).isEqualTo(StoreType.APPLE);
            assertThat(savedEntity.getOrderTxId()).isEqualTo("ORDER_TX_12345");
        }

        @Test
        @DisplayName("[success] 신규 GOOGLE 결제를 저장한다")
        void success_googlePayment() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Payment payment = Payment.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_google_456")
                .orderTxId("GOOGLE_TX_67890")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            // when
            Payment result = adapter.save(payment);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getStoreType()).isEqualTo(StoreType.GOOGLE);
            assertThat(result.getOrderTxId()).isEqualTo("GOOGLE_TX_67890");

            PaymentEntity savedEntity = entityManager.find(PaymentEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getStoreType()).isEqualTo(StoreType.GOOGLE);
        }

        @Test
        @DisplayName("[success] 여러 결제를 저장한다")
        void success_multipleSaves() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Payment payment1 = Payment.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_1")
                .orderTxId("APPLE_TX_1")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            Payment payment2 = Payment.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_2")
                .orderTxId("GOOGLE_TX_2")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            // when
            Payment result1 = adapter.save(payment1);
            Payment result2 = adapter.save(payment2);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result1).isNotNull();
            assertThat(result1.getId()).isNotNull();
            assertThat(result2).isNotNull();
            assertThat(result2.getId()).isNotNull();
            assertThat(result1.getId()).isNotEqualTo(result2.getId());

            PaymentEntity savedEntity1 = entityManager.find(PaymentEntity.class, result1.getId());
            PaymentEntity savedEntity2 = entityManager.find(PaymentEntity.class, result2.getId());
            assertThat(savedEntity1).isNotNull();
            assertThat(savedEntity2).isNotNull();
        }

        @Test
        @DisplayName("[success] ID가 자동 생성된다")
        void success_autoGeneratedId() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Payment payment = Payment.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_auto")
                .orderTxId("APPLE_TX_AUTO_ID")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            // when
            Payment result = adapter.save(payment);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getId()).isGreaterThan(0L);
        }
    }

    @Nested
    @DisplayName("[findById] ID로 결제를 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] ID로 결제를 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            PaymentEntity payment = PaymentEntity.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();
            entityManager.persist(payment);
            entityManager.flush();
            entityManager.clear();

            // when
            Payment result = adapter.findById(payment.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(payment.getId());
            assertThat(result.getAccountId()).isEqualTo(1L);
            assertThat(result.getStoreType()).isEqualTo(StoreType.APPLE);
            assertThat(result.getOrderTxId()).isEqualTo("APPLE_TX_12345");
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
        }

        @Test
        @DisplayName("[success] 여러 결제 중 특정 ID만 조회한다")
        void success_specificPayment() {
            // given
            LocalDateTime now = LocalDateTime.now();
            PaymentEntity payment1 = PaymentEntity.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_1")
                .orderTxId("APPLE_TX_1")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            PaymentEntity payment2 = PaymentEntity.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_2")
                .orderTxId("GOOGLE_TX_2")
                .status(PaymentStatus.PAID)
                .storeProcessDt(now)
                .regDt(now)
                .build();

            entityManager.persist(payment1);
            entityManager.persist(payment2);
            entityManager.flush();
            entityManager.clear();

            // when
            Payment result = adapter.findById(payment1.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(payment1.getId());
            assertThat(result.getStoreType()).isEqualTo(StoreType.APPLE);
            assertThat(result.getOrderTxId()).isEqualTo("APPLE_TX_1");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 ID로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                CustomBusinessException.class,
                () -> adapter.findById(9999L)
            );
        }
    }
}
