package com.odcloud.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Subscription;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SubscriptionPlanChangeCalculatorTest {

    private final SubscriptionPlanChangeCalculator calculator = new SubscriptionPlanChangeCalculator();

    @Nested
    @DisplayName("[isUpgrade] 신규 상품가가 더 비싼지 판정하는 메서드")
    class Describe_isUpgrade {

        @Test
        @DisplayName("[success] 신규 가격이 더 크면 true를 반환한다")
        void success_true() {
            // when
            boolean result = calculator.isUpgrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 신규 가격이 같으면 false를 반환한다")
        void success_equal() {
            // when
            boolean result = calculator.isUpgrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 신규 가격이 더 작으면 false를 반환한다")
        void success_lower() {
            // when
            boolean result = calculator.isUpgrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(5000));

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[isDowngrade] 신규 상품가가 더 저렴한지 판정하는 메서드")
    class Describe_isDowngrade {

        @Test
        @DisplayName("[success] 신규 가격이 더 작으면 true를 반환한다")
        void success_true() {
            // when
            boolean result = calculator.isDowngrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(5000));

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 신규 가격이 같으면 false를 반환한다")
        void success_equal() {
            // when
            boolean result = calculator.isDowngrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 신규 가격이 더 크면 false를 반환한다")
        void success_higher() {
            // when
            boolean result = calculator.isDowngrade(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[calculateRemainingValue] 현재 결제 주기의 잔여가치를 계산하는 메서드")
    class Describe_calculateRemainingValue {

        @Test
        @DisplayName("[success] 잔여일수가 정확히 절반이면 현재가의 절반을 반환한다")
        void success_halfPeriod() {
            // given: 2026-04-01 ~ 2026-05-01(30일 주기), today = 2026-04-16(잔여 15일)
            Subscription subscription = Subscription.builder()
                .expiredDate(LocalDate.of(2026, 5, 1))
                .build();
            LocalDate today = LocalDate.of(2026, 4, 16);

            // when
            BigDecimal result = calculator.calculateRemainingValue(
                BigDecimal.valueOf(100000), subscription, today);

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(50000));
        }

        @Test
        @DisplayName("[success] 나누어떨어지지 않는 경우 소수점을 반올림한다")
        void success_roundingHalfUp() {
            // given: 2026-07-01 ~ 2026-08-01(31일 주기), today = 2026-07-22(잔여 10일)
            Subscription subscription = Subscription.builder()
                .expiredDate(LocalDate.of(2026, 8, 1))
                .build();
            LocalDate today = LocalDate.of(2026, 7, 22);

            // when
            BigDecimal result = calculator.calculateRemainingValue(
                BigDecimal.valueOf(100000), subscription, today);

            // then: 100000 * 10 / 31 = 32258.064... -> HALF_UP 32258
            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(32258));
        }

        @Test
        @DisplayName("[error] 오늘이 만료일과 같으면 잔여가치는 0이다")
        void error_todayEqualsExpiredDate() {
            // given
            LocalDate expiredDate = LocalDate.of(2026, 5, 1);
            Subscription subscription = Subscription.builder()
                .expiredDate(expiredDate)
                .build();

            // when
            BigDecimal result = calculator.calculateRemainingValue(
                BigDecimal.valueOf(100000), subscription, expiredDate);

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("[error] 오늘이 만료일을 지났으면 잔여일수를 0으로 클램프하여 잔여가치는 0이다")
        void error_todayAfterExpiredDate() {
            // given
            Subscription subscription = Subscription.builder()
                .expiredDate(LocalDate.of(2026, 5, 1))
                .build();
            LocalDate today = LocalDate.of(2026, 5, 10);

            // when
            BigDecimal result = calculator.calculateRemainingValue(
                BigDecimal.valueOf(100000), subscription, today);

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("[calculateUpgradeChargeAmount] 업그레이드 시 결제할 차액을 계산하는 메서드")
    class Describe_calculateUpgradeChargeAmount {

        @Test
        @DisplayName("[success] 신규가에서 잔여가치를 차감한 금액을 반환한다")
        void success() {
            // when
            BigDecimal result = calculator.calculateUpgradeChargeAmount(
                BigDecimal.valueOf(200000), BigDecimal.valueOf(50000));

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(150000));
        }

        @Test
        @DisplayName("[error] 신규가와 잔여가치가 같으면 0을 반환한다")
        void error_equal() {
            // when
            BigDecimal result = calculator.calculateUpgradeChargeAmount(
                BigDecimal.valueOf(50000), BigDecimal.valueOf(50000));

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("[error] 잔여가치가 신규가보다 크면 0으로 클램프한다")
        void error_remainingValueGreaterThanNewPrice() {
            // when
            BigDecimal result = calculator.calculateUpgradeChargeAmount(
                BigDecimal.valueOf(30000), BigDecimal.valueOf(50000));

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
