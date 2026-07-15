package com.odcloud.application.subscription.service.expire_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeRedisStoragePort;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.constant.CommonConstant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExpireSubscriptionsServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeRedisStoragePort fakeRedisStoragePort;
    private ExpireSubscriptionsService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeRedisStoragePort = new FakeRedisStoragePort();
        service = new ExpireSubscriptionsService(
            fakeSubscriptionStoragePort, fakeGroupStoragePort, fakeRedisStoragePort);
        fakeGroupStoragePort.groupDatabase.add(Group.builder()
            .id(1L)
            .name("그룹")
            .ownerEmail("owner@example.com")
            .storageUsed(0L)
            .storageTotal(CommonConstant.DEFAULT_STORAGE_TOTAL)
            .backupYn("N")
            .build());
    }

    private Subscription subscription(Long id, String status, LocalDate expiredDate) {
        return Subscription.builder()
            .id(id)
            .productId(100L)
            .groupId(1L)
            .buyerId(1L)
            .status(status)
            .expiredDate(expiredDate)
            .build();
    }

    private Subscription subscription(Long id, Long productId, String status,
        LocalDate expiredDate) {
        return Subscription.builder()
            .id(id)
            .productId(productId)
            .groupId(1L)
            .buyerId(1L)
            .status(status)
            .expiredDate(expiredDate)
            .build();
    }

    @Nested
    @DisplayName("[expire] 구독 만료 처리")
    class Describe_expire {

        @Test
        @DisplayName("[success] EXP_PENDING 이면서 만료일이 지난 구독의 status 를 EXPIRED 로 변경한다")
        void success() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "EXP_PENDING", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failCount()).isZero();
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
        }

        @Test
        @DisplayName("[skip] 만료일이 지나지 않았거나 EXP_PENDING/DOWN_PENDING 이 아니면 대상이 아니다")
        void skip_notTarget() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "EXP_PENDING", LocalDate.now().plusDays(1)));
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(2L, "ACTIVE", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isZero();
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXP_PENDING");
            assertThat(fakeSubscriptionStoragePort.findById(2L).getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] DOWN_PENDING 이면서 만료일이 지난 구독의 status 를 EXPIRED 로 변경한다")
        void success_downPending() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "DOWN_PENDING", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
        }

        @Test
        @DisplayName("[success] 백업 상품(productId=1) 만료 후 잔여 활성 백업 구독이 없으면 그룹 backupYn 을 N 으로 원복한다")
        void success_revertBackupYnWhenNoRemainingBackup() {
            // given
            fakeGroupStoragePort.findById(1L).updateBackupYn("Y");
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, CommonConstant.BACKUP_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGroupStoragePort.findById(1L).getBackupYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] 50GB(product2) 구독이 만료되어도 100GB(product3) 구독이 남아있으면 storageTotal 을 100GB 로 유지한다")
        void success_keep100GBWhenProduct2Expires() {
            // given
            fakeGroupStoragePort.findById(1L).updateStorageTotal(CommonConstant.STORAGE_50GB);
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, CommonConstant.STORAGE_50GB_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));
            // 같은 그룹의 100GB 구독은 아직 활성 상태로 남아있다 (실제 subscriptionDatabase 상태로 표현)
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(2L, CommonConstant.STORAGE_100GB_PRODUCT_ID, "ACTIVE",
                    LocalDate.now().plusMonths(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGroupStoragePort.findById(1L).getStorageTotal())
                .isEqualTo(CommonConstant.STORAGE_100GB);
        }

        @Test
        @DisplayName("[success] 같은 그룹의 product2, product3 구독이 한 배치에서 순차적으로 만료되면 "
            + "먼저 처리된 구독이 이후 잔여 활성 구독 조회에서 제외되어 최종 storageTotal 이 올바르게 계산된다")
        void success_sequentialExpiryInSameBatchExcludesAlreadyExpired() {
            // given: product2(50GB), product3(100GB) 구독이 모두 같은 배치에서 만료 대상이다
            fakeGroupStoragePort.findById(1L).updateStorageTotal(CommonConstant.STORAGE_100GB);
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, CommonConstant.STORAGE_50GB_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(2L, CommonConstant.STORAGE_100GB_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then: product2 처리 시점에는 product3 가 아직 활성이라 100GB 로 유지되지만,
            // product3 까지 처리되고 나면 잔여 활성 스토리지 구독이 없어 기본값으로 원복되어야 한다
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
            assertThat(fakeSubscriptionStoragePort.findById(2L).getStatus()).isEqualTo("EXPIRED");
            assertThat(fakeGroupStoragePort.findById(1L).getStorageTotal())
                .isEqualTo(CommonConstant.DEFAULT_STORAGE_TOTAL);
        }

        @Test
        @DisplayName("[success] 잔여 활성 스토리지 구독이 없으면 storageTotal 을 기본값(3GB)으로 원복한다")
        void success_revertStorageToDefaultWhenNoneRemaining() {
            // given
            fakeGroupStoragePort.findById(1L).updateStorageTotal(CommonConstant.STORAGE_100GB);
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, CommonConstant.STORAGE_100GB_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGroupStoragePort.findById(1L).getStorageTotal())
                .isEqualTo(CommonConstant.DEFAULT_STORAGE_TOTAL);
        }

        @Test
        @DisplayName("[error] 그룹 갱신 중 실패해도 다른 구독 처리에 영향을 주지 않고 failCount 가 증가한다")
        void error_groupUpdateFailureIncreasesFailCount() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, CommonConstant.BACKUP_PRODUCT_ID, "EXP_PENDING",
                    LocalDate.now().minusDays(1)));
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(2L, 100L, "EXP_PENDING", LocalDate.now().minusDays(1)));
            fakeGroupStoragePort.groupDatabase.clear(); // groupId=1 조회 실패 유도

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.failCount()).isEqualTo(2);
            assertThat(response.successCount()).isZero();
            // 그룹 조회는 실패했지만 구독 상태 변경 자체는 이미 반영되어 있다
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
            assertThat(fakeSubscriptionStoragePort.findById(2L).getStatus()).isEqualTo("EXPIRED");
        }
    }
}
