package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    @Nested
    @DisplayName("[isActive] status가 ACTIVE 인지 확인하는 메서드")
    class Describe_isActive {

        @Test
        @DisplayName("[success] status가 ACTIVE면 true를 반환한다")
        void success_active() {
            // given
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .build();

            // when
            boolean result = subscription.isActive();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 EXP_PENDING이면 false를 반환한다")
        void success_expPending() {
            // given
            Subscription subscription = Subscription.builder()
                .status("EXP_PENDING")
                .build();

            // when
            boolean result = subscription.isActive();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 null이면 false를 반환한다")
        void success_null() {
            // given
            Subscription subscription = Subscription.builder()
                .status(null)
                .build();

            // when
            boolean result = subscription.isActive();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[isCancelable] 구독 취소가 가능한 상태인지 확인하는 메서드")
    class Describe_isCancelable {

        @Test
        @DisplayName("[success] status가 ACTIVE면 true를 반환한다")
        void success_active() {
            // given
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .build();

            // when
            boolean result = subscription.isCancelable();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 DOWN_PENDING이면 true를 반환한다")
        void success_downPending() {
            // given
            Subscription subscription = Subscription.builder()
                .status("DOWN_PENDING")
                .build();

            // when
            boolean result = subscription.isCancelable();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 EXP_PENDING이면 false를 반환한다")
        void success_expPending() {
            // given
            Subscription subscription = Subscription.builder()
                .status("EXP_PENDING")
                .build();

            // when
            boolean result = subscription.isCancelable();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 null이면 false를 반환한다")
        void success_null() {
            // given
            Subscription subscription = Subscription.builder()
                .status(null)
                .build();

            // when
            boolean result = subscription.isCancelable();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[isDownPending] status가 DOWN_PENDING 인지 확인하는 메서드")
    class Describe_isDownPending {

        @Test
        @DisplayName("[success] status가 DOWN_PENDING이면 true를 반환한다")
        void success_downPending() {
            // given
            Subscription subscription = Subscription.builder()
                .status("DOWN_PENDING")
                .build();

            // when
            boolean result = subscription.isDownPending();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 ACTIVE면 false를 반환한다")
        void success_active() {
            // given
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .build();

            // when
            boolean result = subscription.isDownPending();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[cancel] 구독을 취소 대기 상태로 전환하는 메서드")
    class Describe_cancel {

        @Test
        @DisplayName("[success] status를 EXP_PENDING으로 변경하고 modDt를 갱신한다")
        void success() {
            // given
            LocalDateTime initialModDt = LocalDateTime.now().minusDays(1);
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            subscription.cancel();

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(subscription.getStatus()).isEqualTo("EXP_PENDING");
            assertThat(subscription.getModDt()).isAfter(before);
            assertThat(subscription.getModDt()).isBefore(after);
            assertThat(subscription.getModDt()).isAfter(initialModDt);
        }
    }

    @Nested
    @DisplayName("[terminateImmediately] 구독을 즉시 만료 처리하는 메서드")
    class Describe_terminateImmediately {

        @Test
        @DisplayName("[success] status를 EXPIRED로 변경하고 expiredDate와 modDt를 오늘로 갱신한다")
        void success() {
            // given
            LocalDateTime initialModDt = LocalDateTime.now().minusDays(1);
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .expiredDate(LocalDate.now().plusDays(10))
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            subscription.terminateImmediately();

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(subscription.getStatus()).isEqualTo("EXPIRED");
            assertThat(subscription.getExpiredDate()).isEqualTo(LocalDate.now());
            assertThat(subscription.getModDt()).isAfter(before);
            assertThat(subscription.getModDt()).isBefore(after);
            assertThat(subscription.getModDt()).isAfter(initialModDt);
        }
    }

    @Nested
    @DisplayName("[markAsDeletePending] 구독을 해지 예약 상태로 전환하는 메서드")
    class Describe_markAsDeletePending {

        @Test
        @DisplayName("[success] status를 EXP_PENDING 변경하고 expiredDate는 유지한 채 modDt를 갱신한다")
        void success() {
            // given
            LocalDate expiredDate = LocalDate.now().plusDays(15);
            LocalDateTime initialModDt = LocalDateTime.now().minusDays(1);
            Subscription subscription = Subscription.builder()
                .status("ACTIVE")
                .expiredDate(expiredDate)
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            subscription.cancel();

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(subscription.getStatus()).isEqualTo("EXP_PENDING");
            assertThat(subscription.getExpiredDate()).isEqualTo(expiredDate);
            assertThat(subscription.getModDt()).isAfter(before);
            assertThat(subscription.getModDt()).isBefore(after);
            assertThat(subscription.getModDt()).isAfter(initialModDt);
        }
    }
}
