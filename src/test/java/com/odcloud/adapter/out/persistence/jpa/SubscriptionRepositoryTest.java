package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Subscription;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Nested
    @DisplayName("[findExpiredTargets] 만료 대상 구독을 조회하는 메소드")
    class Describe_findExpiredTargets {

        private SubscriptionEntity setUpEntity(String status, LocalDate expiredDate) {
            SubscriptionEntity entity = SubscriptionEntity.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status(status)
                .billingKey("billing-key-123")
                .nextBillingDate(expiredDate)
                .expiredDate(expiredDate)
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();
            return entity;
        }

        @Test
        @DisplayName("[success] status 가 대상 목록에 포함되고 expiredDate 가 기준일 이하이면 조회된다")
        void success() {
            // given
            LocalDate today = LocalDate.now();
            SubscriptionEntity expPending = setUpEntity("EXP_PENDING", today);
            SubscriptionEntity downPending = setUpEntity("DOWN_PENDING", today.minusDays(1));

            // when
            List<Subscription> result =
                repository.findExpiredTargets(List.of("EXP_PENDING", "DOWN_PENDING"), today);

            // then
            assertThat(result)
                .extracting(Subscription::getId)
                .containsExactlyInAnyOrder(expPending.getId(), downPending.getId());
        }

        @Test
        @DisplayName("[success] status 가 대상 목록에 없으면 조회되지 않는다")
        void success_excludesStatusNotInTargets() {
            // given
            LocalDate today = LocalDate.now();
            setUpEntity("ACTIVE", today);

            // when
            List<Subscription> result =
                repository.findExpiredTargets(List.of("EXP_PENDING", "DOWN_PENDING"), today);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] expiredDate 가 기준일보다 미래이면 조회되지 않는다")
        void success_excludesFutureExpiredDate() {
            // given
            LocalDate today = LocalDate.now();
            setUpEntity("EXP_PENDING", today.plusDays(1));

            // when
            List<Subscription> result =
                repository.findExpiredTargets(List.of("EXP_PENDING", "DOWN_PENDING"), today);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("[findByGroupIdAndStatus] 그룹 ID와 status로 구독을 단건 조회하는 메소드")
    class Describe_findByGroupIdAndStatus {

        private SubscriptionEntity setUpEntity(Long groupId, String status) {
            LocalDate date = LocalDate.now();
            SubscriptionEntity entity = SubscriptionEntity.builder()
                .productId(100L)
                .groupId(groupId)
                .buyerId(10L)
                .status(status)
                .billingKey("billing-key-123")
                .nextBillingDate(date)
                .expiredDate(date)
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();
            return entity;
        }

        @Test
        @DisplayName("[success] groupId와 status가 일치하는 구독을 조회한다")
        void success() {
            // given
            SubscriptionEntity entity = setUpEntity(1L, "PENDING");

            // when
            Optional<Subscription> result = repository.findByGroupIdAndStatus(1L, "PENDING")
                .map(SubscriptionEntity::toDomain);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(entity.getId());
        }

        @Test
        @DisplayName("[success] 일치하는 구독이 없으면 빈 Optional을 응답한다")
        void success_empty() {
            // given
            setUpEntity(1L, "ACTIVE");

            // when
            Optional<Subscription> result = repository.findByGroupIdAndStatus(1L, "PENDING")
                .map(SubscriptionEntity::toDomain);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("[deleteById] 구독을 물리 삭제하는 메소드")
    class Describe_deleteById {

        @Test
        @DisplayName("[success] 대상 구독을 삭제한다")
        void success() {
            // given
            SubscriptionEntity entity = SubscriptionEntity.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status("PENDING")
                .billingKey("billing-key-123")
                .nextBillingDate(LocalDate.now())
                .expiredDate(LocalDate.now())
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            repository.deleteById(entity.getId());
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(entityManager.find(SubscriptionEntity.class, entity.getId())).isNull();
        }
    }
}
