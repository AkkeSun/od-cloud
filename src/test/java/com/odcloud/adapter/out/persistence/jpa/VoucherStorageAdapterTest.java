package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
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
class VoucherStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    VoucherStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM VoucherEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM PaymentEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 바우처를 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 STORAGE_PLUS 바우처를 저장한다")
        void success_storagePlus() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .groupId(10L)
                .memo("프리미엄 플랜 구매")
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getPaymentId()).isEqualTo(100L);
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_PLUS);
            assertThat(result.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
            assertThat(result.getAccountId()).isEqualTo(1L);
            assertThat(result.getGroupId()).isEqualTo(10L);
            assertThat(result.getMemo()).isEqualTo("프리미엄 플랜 구매");
            assertThat(result.getStartAt()).isNotNull();
            assertThat(result.getEndDt()).isNotNull();

            VoucherEntity savedEntity = entityManager.find(VoucherEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getVoucherType()).isEqualTo(VoucherType.STORAGE_PLUS);
            assertThat(savedEntity.getGroupId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("[success] 신규 STORAGE_BASIC 바우처를 저장한다")
        void success_storageBasic() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(200L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(2L)
                .groupId(20L)
                .memo("베이직 플랜")
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);
            assertThat(result.getGroupId()).isEqualTo(20L);

            VoucherEntity savedEntity = entityManager.find(VoucherEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);
        }

        @Test
        @DisplayName("[success] 신규 STORAGE_BASIC 바우처를 저장한다")
        void success_storage_basic() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(300L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(3L)
                .groupId(30L)
                .startAt(now)
                .endDt(VoucherType.STORAGE_BASIC.calculateEndDt(now))
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);

            VoucherEntity savedEntity = entityManager.find(VoucherEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
        }

        @Test
        @DisplayName("[success] 신규 STORAGE_PLUS 바우처를 저장한다")
        void success_storage_plus() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(400L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(4L)
                .groupId(40L)
                .startAt(now)
                .endDt(VoucherType.STORAGE_PLUS.calculateEndDt(now))
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_PLUS);

            VoucherEntity savedEntity = entityManager.find(VoucherEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
        }

        @Test
        @DisplayName("[success] 신규 ADVERTISE 바우처를 저장한다")
        void success_advertise() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(500L)
                .voucherType(VoucherType.ADVERTISE_30)
                .status(VoucherStatus.ACTIVE)
                .accountId(5L)
                .groupId(null) // ADVERTISE는 groupId가 없을 수 있음
                .memo("광고 제거")
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.ADVERTISE_30);
            assertThat(result.getGroupId()).isNull();

            VoucherEntity savedEntity = entityManager.find(VoucherEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getVoucherType()).isEqualTo(VoucherType.ADVERTISE_30);
            assertThat(savedEntity.getGroupId()).isNull();
        }

        @Test
        @DisplayName("[success] null 값을 포함한 바우처를 저장한다")
        void success_withNullValues() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .paymentId(600L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(6L)
                .groupId(60L)
                .memo(null)
                .startAt(now)
                .endDt(now.plusDays(30))
                .modDt(null)
                .regDt(now)
                .build();

            // when
            Voucher result = adapter.save(voucher);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getMemo()).isNull();
            assertThat(result.getModDt()).isNull();
        }

        @Test
        @DisplayName("[success] 여러 바우처를 저장한다")
        void success_multipleSaves() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher1 = Voucher.builder()
                .paymentId(700L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(7L)
                .groupId(70L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            Voucher voucher2 = Voucher.builder()
                .paymentId(800L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(8L)
                .groupId(80L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            // when
            Voucher result1 = adapter.save(voucher1);
            Voucher result2 = adapter.save(voucher2);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result1).isNotNull();
            assertThat(result1.getId()).isNotNull();
            assertThat(result2).isNotNull();
            assertThat(result2.getId()).isNotNull();

            VoucherEntity savedEntity1 = entityManager.find(VoucherEntity.class, result1.getId());
            VoucherEntity savedEntity2 = entityManager.find(VoucherEntity.class, result2.getId());
            assertThat(savedEntity1).isNotNull();
            assertThat(savedEntity2).isNotNull();
        }
    }

    @Nested
    @DisplayName("[findById] ID로 바우처를 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] ID로 바우처를 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            VoucherEntity voucher = VoucherEntity.builder()
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .groupId(10L)
                .memo("테스트 메모")
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();
            entityManager.persist(voucher);
            entityManager.flush();
            entityManager.clear();

            // when
            Voucher result = adapter.findById(voucher.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(voucher.getId());
            assertThat(result.getPaymentId()).isEqualTo(100L);
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_PLUS);
            assertThat(result.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
            assertThat(result.getAccountId()).isEqualTo(1L);
            assertThat(result.getGroupId()).isEqualTo(10L);
            assertThat(result.getMemo()).isEqualTo("테스트 메모");
        }

        @Test
        @DisplayName("[success] 여러 바우처 중 특정 ID만 조회한다")
        void success_specificVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            VoucherEntity voucher1 = VoucherEntity.builder()
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .groupId(10L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();
            entityManager.persist(voucher1);

            VoucherEntity voucher2 = VoucherEntity.builder()
                .paymentId(200L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(2L)
                .groupId(20L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();
            entityManager.persist(voucher2);

            entityManager.flush();
            entityManager.clear();

            // when
            Voucher result = adapter.findById(voucher1.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(voucher1.getId());
            assertThat(result.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);
            assertThat(result.getGroupId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("[success] EXPIRED 상태의 바우처도 조회한다")
        void success_expiredVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            VoucherEntity voucher = VoucherEntity.builder()
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.EXPIRED)
                .accountId(1L)
                .groupId(10L)
                .startAt(now.minusDays(60))
                .endDt(now.minusDays(30))
                .regDt(now.minusDays(60))
                .build();
            entityManager.persist(voucher);
            entityManager.flush();
            entityManager.clear();

            // when
            Voucher result = adapter.findById(voucher.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(VoucherStatus.EXPIRED);
        }

        @Test
        @DisplayName("[success] REVOKED 상태의 바우처도 조회한다")
        void success_revokedVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            VoucherEntity voucher = VoucherEntity.builder()
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.REVOKED)
                .accountId(1L)
                .groupId(10L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .modDt(now)
                .regDt(now)
                .build();
            entityManager.persist(voucher);
            entityManager.flush();
            entityManager.clear();

            // when
            Voucher result = adapter.findById(voucher.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(VoucherStatus.REVOKED);
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
