package com.odcloud.application.voucher.service.expire_vouchers;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.fakeClass.FakeVoucherStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExpireVouchersServiceTest {

    private ExpireVouchersService service;
    private FakeVoucherStoragePort voucherStoragePort;

    @BeforeEach
    void setUp() {
        voucherStoragePort = new FakeVoucherStoragePort();
        service = new ExpireVouchersService(voucherStoragePort);
    }

    @Nested
    @DisplayName("[expireVouchers] 만료된 바우처 상태 변경 메서드")
    class Describe_expireVouchers {

        @Test
        @DisplayName("[success] 만료된 ACTIVE 바우처를 EXPIRED 상태로 변경한다")
        void success_expireActiveVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher expiredVoucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();
            voucherStoragePort.database.add(expiredVoucher);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(1);
            Voucher updatedVoucher = voucherStoragePort.findById(1L);
            assertThat(updatedVoucher.getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(updatedVoucher.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 여러 개의 만료된 바우처를 일괄 처리한다")
        void success_expireMultipleVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Voucher expiredVoucher1 = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();

            Voucher expiredVoucher2 = Voucher.builder()
                .id(2L)
                .paymentId(200L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(2L)
                .startAt(now.minusDays(60))
                .endDt(now.minusDays(30))
                .regDt(now.minusDays(60))
                .build();

            Voucher expiredVoucher3 = Voucher.builder()
                .id(3L)
                .paymentId(300L)
                .voucherType(VoucherType.ADVERTISE_30)
                .status(VoucherStatus.ACTIVE)
                .accountId(3L)
                .startAt(now.minusDays(35))
                .endDt(now.minusDays(5))
                .regDt(now.minusDays(35))
                .build();

            voucherStoragePort.database.add(expiredVoucher1);
            voucherStoragePort.database.add(expiredVoucher2);
            voucherStoragePort.database.add(expiredVoucher3);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(3);
            assertThat(voucherStoragePort.findById(1L).getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(voucherStoragePort.findById(2L).getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(voucherStoragePort.findById(3L).getStatus()).isEqualTo(VoucherStatus.EXPIRED);
        }

        @Test
        @DisplayName("[success] 아직 만료되지 않은 ACTIVE 바우처는 변경하지 않는다")
        void success_notExpireActiveVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher activeVoucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(10))
                .endDt(now.plusDays(20))
                .regDt(now.minusDays(10))
                .build();
            voucherStoragePort.database.add(activeVoucher);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(0);
            Voucher unchangedVoucher = voucherStoragePort.findById(1L);
            assertThat(unchangedVoucher.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        }

        @Test
        @DisplayName("[success] 이미 EXPIRED 상태인 바우처는 처리하지 않는다")
        void success_skipAlreadyExpiredVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher alreadyExpiredVoucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.EXPIRED)
                .accountId(1L)
                .startAt(now.minusDays(60))
                .endDt(now.minusDays(30))
                .modDt(now.minusDays(30))
                .regDt(now.minusDays(60))
                .build();
            voucherStoragePort.database.add(alreadyExpiredVoucher);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] REVOKED 상태인 바우처도 endDt가 지났으면 EXPIRED로 변경한다")
        void success_expireRevokedVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher revokedVoucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.REVOKED)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();
            voucherStoragePort.database.add(revokedVoucher);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(1);
            Voucher updatedVoucher = voucherStoragePort.findById(1L);
            assertThat(updatedVoucher.getStatus()).isEqualTo(VoucherStatus.EXPIRED);
        }

        @Test
        @DisplayName("[success] 만료된 바우처가 없으면 0을 반환한다")
        void success_noExpiredVouchers() {
            // given - empty database

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] 만료된 바우처와 유효한 바우처가 섞여있으면 만료된 것만 처리한다")
        void success_mixedVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Voucher expiredVoucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();

            Voucher activeVoucher = Voucher.builder()
                .id(2L)
                .paymentId(200L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.ACTIVE)
                .accountId(2L)
                .startAt(now.minusDays(10))
                .endDt(now.plusDays(20))
                .regDt(now.minusDays(10))
                .build();

            Voucher alreadyExpiredVoucher = Voucher.builder()
                .id(3L)
                .paymentId(300L)
                .voucherType(VoucherType.ADVERTISE_30)
                .status(VoucherStatus.EXPIRED)
                .accountId(3L)
                .startAt(now.minusDays(60))
                .endDt(now.minusDays(30))
                .modDt(now.minusDays(30))
                .regDt(now.minusDays(60))
                .build();

            voucherStoragePort.database.add(expiredVoucher);
            voucherStoragePort.database.add(activeVoucher);
            voucherStoragePort.database.add(alreadyExpiredVoucher);

            // when
            int expiredCount = service.expireVouchers();

            // then
            assertThat(expiredCount).isEqualTo(1);
            assertThat(voucherStoragePort.findById(1L).getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(voucherStoragePort.findById(2L).getStatus()).isEqualTo(VoucherStatus.ACTIVE);
            assertThat(voucherStoragePort.findById(3L).getStatus()).isEqualTo(VoucherStatus.EXPIRED);
        }
    }
}
