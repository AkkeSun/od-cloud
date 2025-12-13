package com.odcloud.adapter.in.update_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.UpdateScheduleCommand;
import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateScheduleRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 개인 스케줄 수정 Request를 Command로 변환한다")
        void success_personalSchedule() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .notificationDt("2025-01-02 13:50:00")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            UpdateScheduleCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.scheduleId()).isEqualTo(1L);
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.content()).isEqualTo("수정된 회의");
            assertThat(command.startDt()).isEqualTo(LocalDateTime.of(2025, 1, 2, 14, 0, 0));
            assertThat(command.notificationDt()).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 13, 50, 0));
        }

        @Test
        @DisplayName("[success] 그룹 스케줄 수정 Request를 Command로 변환한다")
        void success_groupSchedule() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 그룹 회의")
                .startDt("2025-01-02 16:00:00")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            UpdateScheduleCommand command = request.toCommand(2L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.scheduleId()).isEqualTo(2L);
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.content()).isEqualTo("수정된 그룹 회의");
            assertThat(command.notificationDt()).isNull();
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 Request를 Command로 변환한다")
        void success_withoutNotification() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .notificationDt(null)
                .build();

            Account account = mock(Account.class);

            // when
            UpdateScheduleCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.notificationDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .notificationDt("2025-01-02 13:50:00")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.content()).isEqualTo("수정된 회의");
            assertThat(request.startDt()).isEqualTo("2025-01-02 14:00:00");
            assertThat(request.notificationDt()).isEqualTo("2025-01-02 13:50:00");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content(null)
                .startDt(null)
                .notificationDt(null)
                .build();

            // then
            assertThat(request.content()).isNull();
            assertThat(request.startDt()).isNull();
            assertThat(request.notificationDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            UpdateScheduleRequest request1 = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            UpdateScheduleRequest request2 = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateScheduleRequest request1 = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            UpdateScheduleRequest request2 = UpdateScheduleRequest.builder()
                .content("다른 회의")
                .startDt("2025-01-03 14:00:00")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] content()로 값을 조회한다")
        void success_content() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            // when
            String content = request.content();

            // then
            assertThat(content).isEqualTo("수정된 회의");
        }

        @Test
        @DisplayName("[success] startDt()로 값을 조회한다")
        void success_startDt() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            // when
            String startDt = request.startDt();

            // then
            assertThat(startDt).isEqualTo("2025-01-02 14:00:00");
        }

        @Test
        @DisplayName("[success] notificationDt()로 값을 조회한다")
        void success_notificationDt() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .notificationDt("2025-01-02 13:50:00")
                .build();

            // when
            String notificationDt = request.notificationDt();

            // then
            assertThat(notificationDt).isEqualTo("2025-01-02 13:50:00");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
