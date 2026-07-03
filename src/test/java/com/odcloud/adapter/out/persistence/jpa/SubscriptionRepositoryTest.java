package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Subscription;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SubscriptionRepositoryTest extends IntegrationTestSupport {

    @Autowired
    SubscriptionRepository repository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM SubscriptionEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 구독을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] expiredDate 를 포함한 신규 구독을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDate nextBillingDate = LocalDate.now().plusMonths(1);
            LocalDateTime expiredDate = nextBillingDate.atStartOfDay();

            Subscription subscription = Subscription.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status("ACTIVE")
                .billingKey("billing-key-123")
                .nextBillingDate(nextBillingDate)
                .expiredDate(expiredDate)
                .regDt(now)
                .build();

            // when
            Subscription result = repository.save(subscription);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getExpiredDate()).isEqualTo(expiredDate);

            SubscriptionEntity savedEntity = entityManager.find(SubscriptionEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getExpiredDate()).isEqualTo(expiredDate);
        }

        @Test
        @DisplayName("[success] 기존 구독을 조회하면 저장된 expiredDate 가 그대로 조회된다")
        void success_readBack() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredDate = LocalDate.now().plusMonths(1).atStartOfDay();

            SubscriptionEntity existing = SubscriptionEntity.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status("ACTIVE")
                .billingKey("billing-key-123")
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .expiredDate(expiredDate)
                .regDt(now)
                .build();
            entityManager.persist(existing);
            entityManager.flush();
            entityManager.clear();

            // when
            SubscriptionEntity found = entityManager.find(SubscriptionEntity.class, existing.getId());

            // then
            assertThat(found).isNotNull();
            assertThat(found.toDomain().getExpiredDate()).isEqualTo(expiredDate);
        }
    }
}
