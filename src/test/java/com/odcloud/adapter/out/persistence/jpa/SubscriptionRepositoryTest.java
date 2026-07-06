package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Subscription;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
            LocalDate expiredDate = nextBillingDate;

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
            LocalDate expiredDate = LocalDate.now().plusMonths(1);

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

    @Nested
    @DisplayName("[findByRenewTargets] 갱신 대상 구독을 조회하는 메소드")
    class Describe_findByRenewTargets {

        private SubscriptionEntity setUpEntity(String status, LocalDate nextBillingDate) {
            SubscriptionEntity entity = SubscriptionEntity.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status(status)
                .billingKey("billing-key-123")
                .nextBillingDate(nextBillingDate)
                .expiredDate(nextBillingDate)
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();
            return entity;
        }

        @Test
        @DisplayName("[success] status 가 대상 목록에 포함되고 nextBillingDate 가 기준일 이하이면 조회된다")
        void success() {
            // given
            LocalDate today = LocalDate.now();
            SubscriptionEntity active = setUpEntity("ACTIVE", today);
            SubscriptionEntity pending = setUpEntity("PENDING", today.minusDays(1));

            // when
            List<Subscription> result = repository.findByRenewTargets(List.of("ACTIVE", "PENDING"), today);

            // then
            assertThat(result)
                .extracting(Subscription::getId)
                .containsExactlyInAnyOrder(active.getId(), pending.getId());
        }

        @Test
        @DisplayName("[success] status 가 대상 목록에 없으면 조회되지 않는다")
        void success_excludesStatusNotInTargets() {
            // given
            LocalDate today = LocalDate.now();
            setUpEntity("EXPIRED", today);

            // when
            List<Subscription> result = repository.findByRenewTargets(List.of("ACTIVE", "PENDING"), today);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] nextBillingDate 가 기준일보다 미래이면 조회되지 않는다")
        void success_excludesFutureNextBillingDate() {
            // given
            LocalDate today = LocalDate.now();
            setUpEntity("ACTIVE", today.plusDays(1));

            // when
            List<Subscription> result = repository.findByRenewTargets(List.of("ACTIVE", "PENDING"), today);

            // then
            assertThat(result).isEmpty();
        }
    }
}
