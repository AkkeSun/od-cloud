package com.odcloud.application.service.update_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.UpdateScheduleCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Schedule;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateScheduleServiceTest {

    private FakeScheduleStoragePort fakeScheduleStoragePort;
    private UpdateScheduleService updateScheduleService;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        updateScheduleService = new UpdateScheduleService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[update] 스케줄 수정")
    class Describe_update {

        @Test
        @DisplayName("[success] 정상적으로 개인 스케줄을 수정한다")
        void success_personalSchedule() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(Arrays.asList())
                .build();

            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("user@example.com")
                .content("기존 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationDt(LocalDateTime.of(2025, 1, 1, 9, 50))
                .notificationYn("N")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeScheduleStoragePort.database.add(schedule);

            LocalDateTime newStartDt = LocalDateTime.of(2025, 1, 2, 14, 0);
            LocalDateTime newEndDt = LocalDateTime.of(2025, 1, 2, 15, 0);
            LocalDateTime newNotificationDt = LocalDateTime.of(2025, 1, 2, 13, 50);

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(1L)
                .account(account)
                .content("수정된 회의")
                .startDt(newStartDt)
                .endDt(newEndDt)
                .notificationDt(newNotificationDt)
                .build();

            // when
            UpdateScheduleServiceResponse response = updateScheduleService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(2); // 기존 1개 + 수정된 1개 (save 시 새로 추가됨)
            Schedule updatedSchedule = fakeScheduleStoragePort.database.get(1);
            assertThat(updatedSchedule.getId()).isEqualTo(1L);
            assertThat(updatedSchedule.getContent()).isEqualTo("수정된 회의");
            assertThat(updatedSchedule.getStartDt()).isEqualTo(newStartDt);
            assertThat(updatedSchedule.getEndDt()).isEqualTo(newEndDt);
            assertThat(updatedSchedule.getNotificationDt()).isEqualTo(newNotificationDt);
            assertThat(updatedSchedule.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 알림 시간을 null로 수정한다")
        void success_updateNotificationToNull() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .groups(Arrays.asList())
                .build();

            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("user@example.com")
                .content("기존 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationDt(LocalDateTime.of(2025, 1, 1, 9, 50))
                .notificationYn("N")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeScheduleStoragePort.database.add(schedule);

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(1L)
                .account(account)
                .content("수정된 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationDt(null)
                .build();

            // when
            UpdateScheduleServiceResponse response = updateScheduleService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(2);
            Schedule updatedSchedule = fakeScheduleStoragePort.database.get(1);
            assertThat(updatedSchedule.getId()).isEqualTo(1L);
            assertThat(updatedSchedule.getNotificationDt()).isNull();
        }

        @Test
        @DisplayName("[success] 정상적으로 그룹 스케줄을 수정한다")
        void success_groupSchedule() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(Arrays.asList(group))
                .build();

            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("owner@example.com")
                .groupId("group-123")
                .content("기존 그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationYn("N")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeScheduleStoragePort.database.add(schedule);

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(1L)
                .account(account)
                .content("수정된 그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 2, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 2, 15, 0))
                .notificationDt(null)
                .build();

            // when
            UpdateScheduleServiceResponse response = updateScheduleService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(2);
            Schedule updatedSchedule = fakeScheduleStoragePort.database.get(1);
            assertThat(updatedSchedule.getId()).isEqualTo(1L);
            assertThat(updatedSchedule.getContent()).isEqualTo("수정된 그룹 회의");
            assertThat(updatedSchedule.getGroupId()).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[failure] 권한이 없는 그룹의 스케줄 수정 시도하면 예외가 발생한다")
        void failure_accessDeniedForGroup() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(Arrays.asList(group))
                .build();

            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("owner@example.com")
                .groupId("other-group")
                .content("다른 그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationYn("N")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeScheduleStoragePort.database.add(schedule);

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(1L)
                .account(account)
                .content("수정된 회의")
                .startDt(LocalDateTime.of(2025, 1, 2, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 2, 15, 0))
                .notificationDt(null)
                .build();

            // when & then
            assertThatThrownBy(() -> updateScheduleService.update(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[failure] 다른 사용자의 개인 스케줄 수정 시도하면 예외가 발생한다")
        void failure_accessDeniedForPersonal() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(Arrays.asList())
                .build();

            Schedule schedule = Schedule.builder()
                .id(1L)
                .writerEmail("other@example.com")
                .content("다른 사용자의 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDt(LocalDateTime.of(2025, 1, 1, 11, 0))
                .notificationYn("N")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeScheduleStoragePort.database.add(schedule);

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(1L)
                .account(account)
                .content("수정된 회의")
                .startDt(LocalDateTime.of(2025, 1, 2, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 2, 15, 0))
                .notificationDt(null)
                .build();

            // when & then
            assertThatThrownBy(() -> updateScheduleService.update(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 스케줄 수정 시도하면 예외가 발생한다")
        void failure_scheduleNotFound() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .groups(Arrays.asList())
                .build();

            UpdateScheduleCommand command = UpdateScheduleCommand.builder()
                .scheduleId(999L)
                .account(account)
                .content("수정된 회의")
                .startDt(LocalDateTime.of(2025, 1, 2, 14, 0))
                .endDt(LocalDateTime.of(2025, 1, 2, 15, 0))
                .notificationDt(null)
                .build();

            // when & then
            assertThatThrownBy(() -> updateScheduleService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_NOT_FOUND_SCHEDULE);
        }
    }
}
