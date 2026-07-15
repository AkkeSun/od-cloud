package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
class SubscriptionStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    SubscriptionStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM SubscriptionEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ProductEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM GroupEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    private SubscriptionEntity setUpEntity(String status) {
        return setUpEntity(status, LocalDate.now().plusMonths(1));
    }

    private SubscriptionEntity setUpEntity(String status, LocalDate nextBillingDate) {
        LocalDateTime now = LocalDateTime.now();
        SubscriptionEntity entity = SubscriptionEntity.builder()
            .productId(100L)
            .groupId(1L)
            .buyerId(10L)
            .status(status)
            .billingKey("billing-key-123")
            .nextBillingDate(nextBillingDate)
            .expiredDate(nextBillingDate)
            .regDt(now)
            .build();
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("[findById] 구독을 단건 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] 존재하는 구독을 조회한다")
        void success() {
            // given
            SubscriptionEntity entity = setUpEntity("ACTIVE");

            // when
            Subscription result = adapter.findById(entity.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
            assertThat(result.getBuyerId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독 ID로 조회하면 예외가 발생한다")
        void error_notFound() {
            // when & then
            assertThatThrownBy(() -> adapter.findById(999L))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION);
        }
    }

    @Nested
    @DisplayName("[findByIdForUpdate] 비관적 쓰기 락으로 구독을 단건 조회하는 메소드")
    class Describe_findByIdForUpdate {

        @Test
        @DisplayName("[success] 존재하는 구독을 조회한다")
        void success() {
            // given
            SubscriptionEntity entity = setUpEntity("ACTIVE");

            // when
            Subscription result = adapter.findByIdForUpdate(entity.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
            assertThat(result.getBuyerId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독 ID로 조회하면 예외가 발생한다")
        void error_notFound() {
            // when & then
            assertThatThrownBy(() -> adapter.findByIdForUpdate(999L))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION);
        }
    }

    @Nested
    @DisplayName("[findByRenewTargets] 갱신 대상 구독을 조회하는 메소드")
    class Describe_findByRenewTargets {

        @Test
        @DisplayName("[success] ACTIVE, PENDING 상태이고 nextBillingDate 가 기준일 이하인 구독을 조회한다")
        void success() {
            // given
            LocalDate today = LocalDate.now();
            SubscriptionEntity active = setUpEntity("ACTIVE", today);
            SubscriptionEntity pending = setUpEntity("PENDING", today.minusDays(1));
            setUpEntity("EXPIRED", today);
            setUpEntity("ACTIVE", today.plusDays(1));

            // when
            List<Subscription> result = adapter.findByRenewTargets(today);

            // then
            assertThat(result)
                .extracting(Subscription::getId)
                .containsExactlyInAnyOrder(active.getId(), pending.getId());
        }

        @Test
        @DisplayName("[success] 대상이 없으면 빈 리스트를 응답한다")
        void success_empty() {
            // given
            setUpEntity("EXPIRED", LocalDate.now());

            // when
            List<Subscription> result = adapter.findByRenewTargets(LocalDate.now());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("[findExpiredTargets] 만료 대상 구독을 조회하는 메소드")
    class Describe_findExpiredTargets {

        @Test
        @DisplayName("[success] EXP_PENDING, DOWN_PENDING 상태이고 expiredDate 가 기준일 이하인 구독을 조회한다")
        void success() {
            // given
            LocalDate today = LocalDate.now();
            SubscriptionEntity expPending = setUpEntity("EXP_PENDING", today);
            SubscriptionEntity downPending = setUpEntity("DOWN_PENDING", today.minusDays(1));
            setUpEntity("ACTIVE", today);
            setUpEntity("EXP_PENDING", today.plusDays(1));

            // when
            List<Subscription> result = adapter.findExpiredTargets(today);

            // then
            assertThat(result)
                .extracting(Subscription::getId)
                .containsExactlyInAnyOrder(expPending.getId(), downPending.getId());
        }

        @Test
        @DisplayName("[success] 대상이 없으면 빈 리스트를 응답한다")
        void success_empty() {
            // given
            setUpEntity("ACTIVE", LocalDate.now());

            // when
            List<Subscription> result = adapter.findExpiredTargets(LocalDate.now());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("[findByGroupIdAndStatus] 그룹 ID와 status로 구독을 단건 조회하는 메소드")
    class Describe_findByGroupIdAndStatus {

        @Test
        @DisplayName("[success] groupId와 status가 일치하는 구독을 조회한다")
        void success() {
            // given
            SubscriptionEntity entity = setUpEntity("PENDING");

            // when
            Optional<Subscription> result = adapter.findByGroupIdAndStatus(1L, "PENDING");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(entity.getId());
        }

        @Test
        @DisplayName("[success] 일치하는 구독이 없으면 빈 Optional을 응답한다")
        void success_empty() {
            // given
            setUpEntity("ACTIVE");

            // when
            Optional<Subscription> result = adapter.findByGroupIdAndStatus(1L, "PENDING");

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
            SubscriptionEntity entity = setUpEntity("PENDING");

            // when
            adapter.deleteById(entity.getId());
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(entityManager.find(SubscriptionEntity.class, entity.getId())).isNull();
        }
    }

    @Nested
    @DisplayName("[save] 구독을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 기존 구독의 status 변경사항을 저장(merge)한다")
        void success_updateStatus() {
            // given
            SubscriptionEntity entity = setUpEntity("ACTIVE");
            Subscription found = adapter.findById(entity.getId());
            found.cancel();

            // when
            adapter.save(found);
            entityManager.flush();
            entityManager.clear();

            // then
            SubscriptionEntity updatedEntity = entityManager.find(SubscriptionEntity.class, entity.getId());
            assertThat(updatedEntity).isNotNull();
            assertThat(updatedEntity.getStatus()).isEqualTo("EXP_PENDING");
        }
    }

    @Nested
    @DisplayName("[findActiveByGroupIds] 그룹의 활성(ACTIVE/EXP_PENDING/DOWN_PENDING) 구독을 상품별로 조회하는 메소드")
    class Describe_findActiveByGroupIds {

        @Test
        @DisplayName("[success] EXPIRED 로 변경된 구독은 결과에서 제외되고, 남은 활성 구독만 조회된다")
        void success_excludesExpiredSubscription() {
            // given
            LocalDateTime now = LocalDateTime.now();

            GroupEntity group = GroupEntity.builder()
                .ownerEmail("owner@example.com")
                .name("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity buyer = AccountEntity.builder()
                .email("buyer@example.com")
                .nickname("buyer")
                .picture("https://example.com/pic.jpg")
                .modDt(now)
                .regDt(now)
                .build();
            entityManager.persist(buyer);

            ProductEntity storage50GB = ProductEntity.builder()
                .productName("CLOUD_50GB")
                .price(BigDecimal.valueOf(5500))
                .modDt(now)
                .regDt(now)
                .build();
            entityManager.persist(storage50GB);

            ProductEntity storage100GB = ProductEntity.builder()
                .productName("CLOUD_100GB")
                .price(BigDecimal.valueOf(9900))
                .modDt(now)
                .regDt(now)
                .build();
            entityManager.persist(storage100GB);
            entityManager.flush();

            Long groupId = group.getId();

            // 50GB 구독은 이미 만료 처리(EXPIRED)되어 결과에서 제외되어야 한다
            entityManager.persist(SubscriptionEntity.builder()
                .productId(storage50GB.getId())
                .groupId(groupId)
                .buyerId(buyer.getId())
                .status("EXPIRED")
                .billingKey("billing-key-1")
                .nextBillingDate(LocalDate.now())
                .expiredDate(LocalDate.now())
                .regDt(now)
                .build());

            // 100GB 구독은 아직 활성 상태로 남아있다
            entityManager.persist(SubscriptionEntity.builder()
                .productId(storage100GB.getId())
                .groupId(groupId)
                .buyerId(buyer.getId())
                .status("ACTIVE")
                .billingKey("billing-key-2")
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .expiredDate(LocalDate.now().plusMonths(1))
                .regDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            List<SubscriptionDetail> result = adapter.findActiveByGroupIds(List.of(groupId));

            // then
            SubscriptionDetail expiredProductRow = result.stream()
                .filter(detail -> detail.productId().equals(storage50GB.getId()))
                .findFirst()
                .orElseThrow();
            assertThat(expiredProductRow.subscriptionId()).isNull();

            SubscriptionDetail activeProductRow = result.stream()
                .filter(detail -> detail.productId().equals(storage100GB.getId()))
                .findFirst()
                .orElseThrow();
            assertThat(activeProductRow.subscriptionId()).isNotNull();
            assertThat(activeProductRow.status()).isEqualTo("ACTIVE");
            assertThat(activeProductRow.groupId()).isEqualTo(groupId);
        }

        @Test
        @DisplayName("[success] 상품이 없으면 빈 리스트를 응답한다")
        void success_emptyGroupIds() {
            // when
            List<SubscriptionDetail> result = adapter.findActiveByGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }
}
