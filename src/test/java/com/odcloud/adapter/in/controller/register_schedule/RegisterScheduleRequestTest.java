package com.odcloud.adapter.in.controller.register_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterScheduleRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 개인 스케줄 Request를 Command로 변환한다")
        void success_personalSchedule() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .notificationDt("2025-01-01 09:50:00")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            RegisterScheduleCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.content()).isEqualTo("개인 회의");
            assertThat(command.startDt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0, 0));
            assertThat(command.notificationDt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 9, 50, 0));
            assertThat(command.groupId()).isNull();
        }

        @Test
        @DisplayName("[success] 그룹 스케줄 Request를 Command로 변환한다")
        void success_groupSchedule() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("그룹 회의")
                .startDt("2025-01-01 14:00:00")
                .groupId("group-123")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            RegisterScheduleCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.content()).isEqualTo("그룹 회의");
            assertThat(command.groupId()).isEqualTo("group-123");
            assertThat(command.notificationDt()).isNull();
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 Request를 Command로 변환한다")
        void success_withoutNotification() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("회의")
                .startDt("2025-01-01 10:00:00")
                .notificationDt(null)
                .build();

            Account account = mock(Account.class);

            // when
            RegisterScheduleCommand command = request.toCommand(account);

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
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .groupId("group-123")
                .notificationDt("2025-01-01 09:50:00")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.content()).isEqualTo("개인 회의");
            assertThat(request.startDt()).isEqualTo("2025-01-01 10:00:00");
            assertThat(request.groupId()).isEqualTo("group-123");
            assertThat(request.notificationDt()).isEqualTo("2025-01-01 09:50:00");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content(null)
                .startDt(null)
                .groupId(null)
                .notificationDt(null)
                .build();

            // then
            assertThat(request.content()).isNull();
            assertThat(request.startDt()).isNull();
            assertThat(request.groupId()).isNull();
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
            RegisterScheduleRequest request1 = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            RegisterScheduleRequest request2 = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterScheduleRequest request1 = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            RegisterScheduleRequest request2 = RegisterScheduleRequest.builder()
                .content("그룹 회의")
                .startDt("2025-01-02 10:00:00")
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
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            // when
            String content = request.content();

            // then
            assertThat(content).isEqualTo("개인 회의");
        }

        @Test
        @DisplayName("[success] startDt()로 값을 조회한다")
        void success_startDt() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            // when
            String startDt = request.startDt();

            // then
            assertThat(startDt).isEqualTo("2025-01-01 10:00:00");
        }


        @Test
        @DisplayName("[success] groupId()로 값을 조회한다")
        void success_groupId() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("그룹 회의")
                .startDt("2025-01-01 10:00:00")
                .groupId("group-123")
                .build();

            // when
            String groupId = request.groupId();

            // then
            assertThat(groupId).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] notificationDt()로 값을 조회한다")
        void success_notificationDt() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .notificationDt("2025-01-01 09:50:00")
                .build();

            // when
            String notificationDt = request.notificationDt();

            // then
            assertThat(notificationDt).isEqualTo("2025-01-01 09:50:00");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
