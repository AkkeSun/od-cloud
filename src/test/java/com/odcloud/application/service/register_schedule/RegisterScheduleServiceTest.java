package com.odcloud.application.service.register_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.domain.model.ScheduleType;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterScheduleServiceTest {

    private RegisterScheduleService registerScheduleService;
    private FakeScheduleStoragePort fakeScheduleStoragePort;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        registerScheduleService = new RegisterScheduleService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[register] 일정 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 개인 일정을 등록한다")
        void success_personalSchedule() {
            // given
            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .title("개인 미팅")
                .description("중요한 미팅")
                .startDt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 15, 11, 0))
                .scheduleType(ScheduleType.PERSONAL)
                .accountId(1L)
                .groupId(null)
                .notificationEnabled(true)
                .notificationMinutes(10)
                .build();

            // when
            RegisterScheduleServiceResponse response = registerScheduleService.register(command);

            // then
            assertThat(response.scheduleId()).isNotNull();
            assertThat(fakeScheduleStoragePort.database).hasSize(1);
            assertThat(fakeScheduleStoragePort.database.get(0).getTitle()).isEqualTo("개인 미팅");
            assertThat(fakeScheduleStoragePort.database.get(0).getScheduleType()).isEqualTo(
                ScheduleType.PERSONAL);
        }

        @Test
        @DisplayName("[success] 그룹 일정을 등록한다")
        void success_groupSchedule() {
            // given
            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .title("팀 회의")
                .description("주간 정기 회의")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 15, 15, 0))
                .scheduleType(ScheduleType.GROUP)
                .accountId(1L)
                .groupId(10L)
                .notificationEnabled(false)
                .notificationMinutes(null)
                .build();

            // when
            RegisterScheduleServiceResponse response = registerScheduleService.register(command);

            // then
            assertThat(response.scheduleId()).isNotNull();
            assertThat(fakeScheduleStoragePort.database).hasSize(1);
            assertThat(fakeScheduleStoragePort.database.get(0).getGroupId()).isEqualTo(10L);
            assertThat(fakeScheduleStoragePort.database.get(0).getScheduleType()).isEqualTo(
                ScheduleType.GROUP);
        }

        @Test
        @DisplayName("[exception] 그룹 일정인데 groupId가 없으면 예외가 발생한다")
        void exception_groupScheduleWithoutGroupId() {
            // given
            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .title("팀 회의")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 15, 15, 0))
                .scheduleType(ScheduleType.GROUP)
                .accountId(1L)
                .groupId(null)
                .build();

            // when & then
            assertThatThrownBy(() -> registerScheduleService.register(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("그룹 일정은 groupId가 필수입니다");
        }
    }
}
