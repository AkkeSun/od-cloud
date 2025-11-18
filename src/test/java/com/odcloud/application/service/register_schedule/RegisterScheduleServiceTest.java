package com.odcloud.application.service.register_schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterScheduleServiceTest {

    private FakeScheduleStoragePort fakeScheduleStoragePort;
    private RegisterScheduleService registerScheduleService;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        registerScheduleService = new RegisterScheduleService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[register] 스케줄 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 정상적으로 개인 스케줄을 등록한다")
        void success_personalSchedule() {
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
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(notificationDt)
                .build();

            // when
            RegisterScheduleServiceResponse response = registerScheduleService.register(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(1);
            assertThat(fakeScheduleStoragePort.database.get(0).getWriterEmail()).isEqualTo("user@example.com");
            assertThat(fakeScheduleStoragePort.database.get(0).getContent()).isEqualTo("개인 회의");
            assertThat(fakeScheduleStoragePort.database.get(0).getStartDt()).isEqualTo(startDt);
            assertThat(fakeScheduleStoragePort.database.get(0).getEndDt()).isEqualTo(endDt);
            assertThat(fakeScheduleStoragePort.database.get(0).getNotificationDt()).isEqualTo(notificationDt);
            assertThat(fakeScheduleStoragePort.database.get(0).getNotificationYn()).isEqualTo("N");
            assertThat(fakeScheduleStoragePort.database.get(0).getGroupId()).isNull();
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 개인 스케줄을 등록한다")
        void success_withoutNotification() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .groups(Arrays.asList())
                .build();

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(null)
                .build();

            // when
            RegisterScheduleServiceResponse response = registerScheduleService.register(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(1);
            assertThat(fakeScheduleStoragePort.database.get(0).getNotificationDt()).isNull();
            assertThat(fakeScheduleStoragePort.database.get(0).getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] 정상적으로 그룹 스케줄을 등록한다")
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

            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            // when
            RegisterScheduleServiceResponse response = registerScheduleService.register(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeScheduleStoragePort.database).hasSize(1);
            assertThat(fakeScheduleStoragePort.database.get(0).getGroupId()).isEqualTo("group-123");
            assertThat(fakeScheduleStoragePort.database.get(0).getContent()).isEqualTo("그룹 회의");
            assertThat(fakeScheduleStoragePort.database.get(0).getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[failure] 권한이 없는 그룹에 스케줄 등록 시도하면 예외가 발생한다")
        void failure_accessDenied() {
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

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("other-group")
                .build();

            // when & then
            assertThatThrownBy(() -> registerScheduleService.register(command))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            assertThat(fakeScheduleStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] 그룹이 없는 사용자가 그룹 스케줄 등록 시도하면 예외가 발생한다")
        void failure_noGroups() {
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

            RegisterScheduleCommand command = RegisterScheduleCommand.builder()
                .account(account)
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .build();

            // when & then
            assertThatThrownBy(() -> registerScheduleService.register(command))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            assertThat(fakeScheduleStoragePort.database).isEmpty();
        }
    }
}
