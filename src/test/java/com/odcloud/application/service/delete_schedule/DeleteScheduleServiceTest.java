package com.odcloud.application.service.delete_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
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

class DeleteScheduleServiceTest {

    private FakeScheduleStoragePort fakeScheduleStoragePort;
    private DeleteScheduleService deleteScheduleService;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        deleteScheduleService = new DeleteScheduleService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[delete] 스케줄 삭제")
    class Describe_delete {

        @Test
        @DisplayName("[success] 정상적으로 개인 스케줄을 삭제한다")
        void success_deletePersonalSchedule() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(Arrays.asList())
                .build();

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand registerCommand = RegisterScheduleCommand.builder()
                .account(account)
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .build();

            Schedule schedule = Schedule.of(registerCommand);
            fakeScheduleStoragePort.save(schedule);
            Long scheduleId = fakeScheduleStoragePort.database.get(0).getId();

            // when
            DeleteScheduleServiceResponse response = deleteScheduleService.delete(scheduleId, account);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 정상적으로 그룹 스케줄을 삭제한다")
        void success_deleteGroupSchedule() {
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

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand registerCommand = RegisterScheduleCommand.builder()
                .account(account)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            Schedule schedule = Schedule.of(registerCommand);
            fakeScheduleStoragePort.save(schedule);
            Long scheduleId = fakeScheduleStoragePort.database.get(0).getId();

            // when
            DeleteScheduleServiceResponse response = deleteScheduleService.delete(scheduleId, account);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] 다른 사용자의 개인 스케줄 삭제 시도하면 예외가 발생한다")
        void failure_deleteOthersPersonalSchedule() {
            // given
            Account owner = Account.builder()
                .id(1L)
                .email("owner@example.com")
                .nickname("Owner")
                .groups(Arrays.asList())
                .build();

            Account other = Account.builder()
                .id(2L)
                .email("other@example.com")
                .nickname("Other")
                .groups(Arrays.asList())
                .build();

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand registerCommand = RegisterScheduleCommand.builder()
                .account(owner)
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .build();

            Schedule schedule = Schedule.of(registerCommand);
            fakeScheduleStoragePort.save(schedule);
            Long scheduleId = fakeScheduleStoragePort.database.get(0).getId();

            // when & then
            assertThatThrownBy(() -> deleteScheduleService.delete(scheduleId, other))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            assertThat(fakeScheduleStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 그룹에 속하지 않은 사용자가 그룹 스케줄 삭제 시도하면 예외가 발생한다")
        void failure_deleteGroupScheduleByNonMember() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .build();

            Account member = Account.builder()
                .id(1L)
                .email("member@example.com")
                .nickname("Member")
                .groups(Arrays.asList(group))
                .build();

            Account nonMember = Account.builder()
                .id(2L)
                .email("other@example.com")
                .nickname("Other")
                .groups(Arrays.asList())
                .build();

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand registerCommand = RegisterScheduleCommand.builder()
                .account(member)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            Schedule schedule = Schedule.of(registerCommand);
            fakeScheduleStoragePort.save(schedule);
            Long scheduleId = fakeScheduleStoragePort.database.get(0).getId();

            // when & then
            assertThatThrownBy(() -> deleteScheduleService.delete(scheduleId, nonMember))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            assertThat(fakeScheduleStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 스케줄 삭제 시도하면 예외가 발생한다")
        void failure_deleteNonExistentSchedule() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .groups(Arrays.asList())
                .build();
            // when & then
            assertThatThrownBy(() -> deleteScheduleService.delete(999L, account))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_SCHEDULE);
        }

        @Test
        @DisplayName("[success] 그룹 멤버라면 다른 사람이 등록한 그룹 스케줄도 삭제할 수 있다")
        void success_deleteGroupScheduleByDifferentMember() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .build();

            Account creator = Account.builder()
                .id(1L)
                .email("creator@example.com")
                .nickname("Creator")
                .groups(Arrays.asList(group))
                .build();

            Account otherMember = Account.builder()
                .id(2L)
                .email("member@example.com")
                .nickname("Member")
                .groups(Arrays.asList(group))
                .build();

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand registerCommand = RegisterScheduleCommand.builder()
                .account(creator)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            Schedule schedule = Schedule.of(registerCommand);
            fakeScheduleStoragePort.save(schedule);
            Long scheduleId = fakeScheduleStoragePort.database.get(0).getId();

            // when
            DeleteScheduleServiceResponse response = deleteScheduleService.delete(scheduleId, otherMember);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).isEmpty();
        }
    }
}
