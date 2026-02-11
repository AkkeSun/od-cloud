package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VoucherTest {

    @Nested
    @DisplayName("[expire] 바우처 만료 메서드")
    class Describe_expire {

        @Test
        @DisplayName("[success] ACTIVE 상태의 바우처를 EXPIRED로 변경한다")
        void success_expireActiveVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();

            // when
            voucher.expire();

            // then
            assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(voucher.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] REVOKED 상태의 바우처도 EXPIRED로 변경할 수 있다")
        void success_expireRevokedVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_PLUS)
                .status(VoucherStatus.REVOKED)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .modDt(now.minusDays(15))
                .regDt(now.minusDays(31))
                .build();

            LocalDateTime previousModDt = voucher.getModDt();

            // when
            voucher.expire();

            // then
            assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.EXPIRED);
            assertThat(voucher.getModDt()).isAfter(previousModDt);
        }

        @Test
        @DisplayName("[success] expire 호출 시 modDt가 현재 시간으로 설정된다")
        void success_setModDtOnExpire() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.ADVERTISE_30)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now.minusDays(31))
                .endDt(now.minusDays(1))
                .regDt(now.minusDays(31))
                .build();

            assertThat(voucher.getModDt()).isNull();

            // when
            voucher.expire();

            // then
            assertThat(voucher.getModDt()).isNotNull();
            assertThat(voucher.getModDt()).isAfterOrEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[revoke] 바우처 취소 메서드")
    class Describe_revoke {

        @Test
        @DisplayName("[success] ACTIVE 상태의 바우처를 REVOKED로 변경한다")
        void success_revokeActiveVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = Voucher.builder()
                .id(1L)
                .paymentId(100L)
                .voucherType(VoucherType.STORAGE_BASIC)
                .status(VoucherStatus.ACTIVE)
                .accountId(1L)
                .startAt(now)
                .endDt(now.plusDays(30))
                .regDt(now)
                .build();

            // when
            voucher.revoke();

            // then
            assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.REVOKED);
            assertThat(voucher.getModDt()).isNotNull();
        }
    }
}
