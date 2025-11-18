package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Nested
    @DisplayName("[of] RegisterScheduleCommand로부터 Schedule을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] 개인 스케줄 Command로부터 Schedule을 생성한다")
        void success_personalSchedule() {
            // given
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("user@example.com");

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(notificationDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            Schedule schedule = Schedule.of(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(schedule).isNotNull();
            assertThat(schedule.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(schedule.getContent()).isEqualTo("개인 회의");
            assertThat(schedule.getStartDt()).isEqualTo(startDt);
            assertThat(schedule.getEndDt()).isEqualTo(endDt);
            assertThat(schedule.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
            assertThat(schedule.getGroupId()).isNull();
            assertThat(schedule.getRegDt()).isAfter(before);
            assertThat(schedule.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 그룹 스케줄 Command로부터 Schedule을 생성한다")
        void success_groupSchedule() {
            // given
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("user@example.com");

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 15, 0);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            // when
            Schedule schedule = Schedule.of(command);

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getGroupId()).isEqualTo("group-123");
            assertThat(schedule.getContent()).isEqualTo("그룹 회의");
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 Schedule을 생성한다")
        void success_withoutNotification() {
            // given
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("user@example.com");

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(null)
                .build();

            // when
            Schedule schedule = Schedule.of(command);

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getNotificationDt()).isNull();
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 Command로부터 Schedule을 생성한다")
        void success_withNullValues() {
            // given
            Account account = Account.builder()
                .email(null)
                .build();

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content(null)
                .startDt(null)
                .endDt(null)
                .groupId(null)
                .notificationDt(null)
                .build();

            // when
            Schedule schedule = Schedule.of(command);

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getWriterEmail()).isNull();
            assertThat(schedule.getContent()).isNull();
            assertThat(schedule.getStartDt()).isNull();
            assertThat(schedule.getEndDt()).isNull();
            assertThat(schedule.getGroupId()).isNull();
            assertThat(schedule.getNotificationDt()).isNull();
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            Schedule schedule = Schedule.builder()
                .id(1L)
                .build();

            // when
            Long id = schedule.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getWriterEmail()로 writerEmail을 조회한다")
        void success_getWriterEmail() {
            // given
            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .build();

            // when
            String writerEmail = schedule.getWriterEmail();

            // then
            assertThat(writerEmail).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("[success] getGroupId()로 groupId를 조회한다")
        void success_getGroupId() {
            // given
            Schedule schedule = Schedule.builder()
                .groupId("group-123")
                .build();

            // when
            String groupId = schedule.getGroupId();

            // then
            assertThat(groupId).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] getContent()로 content를 조회한다")
        void success_getContent() {
            // given
            Schedule schedule = Schedule.builder()
                .content("개인 회의")
                .build();

            // when
            String content = schedule.getContent();

            // then
            assertThat(content).isEqualTo("개인 회의");
        }

        @Test
        @DisplayName("[success] getNotificationDt()로 notificationDt를 조회한다")
        void success_getNotificationDt() {
            // given
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);
            Schedule schedule = Schedule.builder()
                .notificationDt(notificationDt)
                .build();

            // when
            LocalDateTime result = schedule.getNotificationDt();

            // then
            assertThat(result).isEqualTo(notificationDt);
        }

        @Test
        @DisplayName("[success] getNotificationYn()로 notificationYn를 조회한다")
        void success_getNotificationYn() {
            // given
            Schedule schedule = Schedule.builder()
                .notificationYn("Y")
                .build();

            // when
            String result = schedule.getNotificationYn();

            // then
            assertThat(result).isEqualTo("Y");
        }

        @Test
        @DisplayName("[success] getStartDt()로 startDt를 조회한다")
        void success_getStartDt() {
            // given
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            Schedule schedule = Schedule.builder()
                .startDt(startDt)
                .build();

            // when
            LocalDateTime result = schedule.getStartDt();

            // then
            assertThat(result).isEqualTo(startDt);
        }

        @Test
        @DisplayName("[success] getEndDt()로 endDt를 조회한다")
        void success_getEndDt() {
            // given
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            Schedule schedule = Schedule.builder()
                .endDt(endDt)
                .build();

            // when
            LocalDateTime result = schedule.getEndDt();

            // then
            assertThat(result).isEqualTo(endDt);
        }

        @Test
        @DisplayName("[success] getModDt()로 modDt를 조회한다")
        void success_getModDt() {
            // given
            LocalDateTime modDt = LocalDateTime.now();
            Schedule schedule = Schedule.builder()
                .modDt(modDt)
                .build();

            // when
            LocalDateTime result = schedule.getModDt();

            // then
            assertThat(result).isEqualTo(modDt);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime regDt = LocalDateTime.now();
            Schedule schedule = Schedule.builder()
                .regDt(regDt)
                .build();

            // when
            LocalDateTime result = schedule.getRegDt();

            // then
            assertThat(result).isEqualTo(regDt);
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Schedule을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            // when
            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("user@example.com")
                .groupId("group-123")
                .content("회의")
                .notificationDt(notificationDt)
                .notificationYn("N")
                .startDt(startDt)
                .endDt(endDt)
                .modDt(now)
                .regDt(now)
                .build();

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getId()).isEqualTo(1L);
            assertThat(schedule.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(schedule.getGroupId()).isEqualTo("group-123");
            assertThat(schedule.getContent()).isEqualTo("회의");
            assertThat(schedule.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
            assertThat(schedule.getStartDt()).isEqualTo(startDt);
            assertThat(schedule.getEndDt()).isEqualTo(endDt);
            assertThat(schedule.getModDt()).isEqualTo(now);
            assertThat(schedule.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] null 값으로 Schedule을 생성한다")
        void success_nullValues() {
            // when
            Schedule schedule = Schedule.builder()
                .id(null)
                .writerEmail(null)
                .groupId(null)
                .content(null)
                .notificationDt(null)
                .notificationYn(null)
                .startDt(null)
                .endDt(null)
                .modDt(null)
                .regDt(null)
                .build();

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getId()).isNull();
            assertThat(schedule.getWriterEmail()).isNull();
            assertThat(schedule.getGroupId()).isNull();
            assertThat(schedule.getContent()).isNull();
            assertThat(schedule.getNotificationDt()).isNull();
            assertThat(schedule.getNotificationYn()).isNull();
            assertThat(schedule.getStartDt()).isNull();
            assertThat(schedule.getEndDt()).isNull();
            assertThat(schedule.getModDt()).isNull();
            assertThat(schedule.getRegDt()).isNull();
        }

        @Test
        @DisplayName("[success] 개인 스케줄을 Builder로 생성한다")
        void success_personalSchedule() {
            // given
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            // when
            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .build();

            // then
            assertThat(schedule.getGroupId()).isNull();
            assertThat(schedule.getWriterEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("[success] 그룹 스케줄을 Builder로 생성한다")
        void success_groupSchedule() {
            // given
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            // when
            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .groupId("group-123")
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .build();

            // then
            assertThat(schedule.getGroupId()).isEqualTo("group-123");
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 Schedule을 생성한다")
        void success() {
            // when
            Schedule schedule = new Schedule();

            // then
            assertThat(schedule).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 Schedule을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            // when
            Schedule schedule = new Schedule(
                1L,
                "user@example.com",
                "group-123",
                "회의",
                notificationDt,
                "N",
                startDt,
                endDt,
                now,
                now
            );

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getId()).isEqualTo(1L);
            assertThat(schedule.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(schedule.getGroupId()).isEqualTo("group-123");
            assertThat(schedule.getContent()).isEqualTo("회의");
            assertThat(schedule.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(schedule.getNotificationYn()).isEqualTo("N");
            assertThat(schedule.getStartDt()).isEqualTo(startDt);
            assertThat(schedule.getEndDt()).isEqualTo(endDt);
            assertThat(schedule.getModDt()).isEqualTo(now);
            assertThat(schedule.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] null 값으로 AllArgsConstructor로 Schedule을 생성한다")
        void success_nullValues() {
            // when
            Schedule schedule = new Schedule(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );

            // then
            assertThat(schedule).isNotNull();
            assertThat(schedule.getId()).isNull();
            assertThat(schedule.getWriterEmail()).isNull();
            assertThat(schedule.getGroupId()).isNull();
            assertThat(schedule.getContent()).isNull();
            assertThat(schedule.getNotificationDt()).isNull();
            assertThat(schedule.getNotificationYn()).isNull();
            assertThat(schedule.getStartDt()).isNull();
            assertThat(schedule.getEndDt()).isNull();
            assertThat(schedule.getModDt()).isNull();
            assertThat(schedule.getRegDt()).isNull();
        }
    }
}
