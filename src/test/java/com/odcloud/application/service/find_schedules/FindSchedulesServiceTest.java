package com.odcloud.application.service.find_schedules;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.dto.ScheduleDto;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Schedule;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindSchedulesServiceTest {

    private FakeScheduleStoragePort fakeScheduleStoragePort;
    private FindSchedulesService findSchedulesService;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        findSchedulesService = new FindSchedulesService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[findSchedules] 일정 조회")
    class Describe_findSchedules {

        @Test
        @DisplayName("[success] 월별 개인 일정을 조회한다 (filterType=PRIVATE)")
        void success_getMonthlyPersonalSchedules() {
            // given
            Account account = createAccount("user@example.com", Arrays.asList());

            // 2025년 1월 일정들
            fakeScheduleStoragePort.database.add(createPersonalSchedule(1L, "user@example.com",
                "1월 1일", LocalDateTime.of(2025, 1, 1, 10, 0)));
            fakeScheduleStoragePort.database.add(createPersonalSchedule(2L, "user@example.com",
                "1월 15일", LocalDateTime.of(2025, 1, 15, 14, 0)));
            fakeScheduleStoragePort.database.add(createPersonalSchedule(3L, "user@example.com",
                "1월 31일", LocalDateTime.of(2025, 1, 31, 16, 0)));

            // 2월 일정
            fakeScheduleStoragePort.database.add(createPersonalSchedule(4L, "user@example.com",
                "2월 1일", LocalDateTime.of(2025, 2, 1, 10, 0)));

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .filterType("PRIVATE")
                .build();

            // when
            FindSchedulesServiceResponse response = findSchedulesService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(3);
            assertThat(response.schedules().get(0).content()).isEqualTo("1월 1일");
            assertThat(response.schedules().get(2).content()).isEqualTo("1월 31일");
        }

        @Test
        @DisplayName("[success] 종료일이 조회 범위와 겹치는 일정도 조회한다 (filterType=PRIVATE)")
        void success_getSchedulesOverlappingEndDate() {
            // given
            Account account = createAccount("user@example.com", Arrays.asList());

            // 12월 25일부터 1월 5일까지 이어지는 일정 (시작일이 조회 범위 밖)
            fakeScheduleStoragePort.database.add(createScheduleWithCustomEndDate(1L, "user@example.com",
                "연말연시 휴가", LocalDateTime.of(2024, 12, 25, 0, 0), LocalDateTime.of(2025, 1, 5, 23, 59)));

            // 1월 28일부터 2월 3일까지 이어지는 일정 (종료일이 조회 범위 밖)
            fakeScheduleStoragePort.database.add(createScheduleWithCustomEndDate(2L, "user@example.com",
                "출장", LocalDateTime.of(2025, 1, 28, 9, 0), LocalDateTime.of(2025, 2, 3, 18, 0)));

            // 1월 내 일정
            fakeScheduleStoragePort.database.add(createPersonalSchedule(3L, "user@example.com",
                "1월 15일", LocalDateTime.of(2025, 1, 15, 14, 0)));

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .filterType("PRIVATE")
                .build();

            // when
            FindSchedulesServiceResponse response = findSchedulesService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(3);
            assertThat(response.schedules().get(0).content()).isEqualTo("연말연시 휴가");
            assertThat(response.schedules().get(1).content()).isEqualTo("1월 15일");
            assertThat(response.schedules().get(2).content()).isEqualTo("출장");
        }

        @Test
        @DisplayName("[success] 특정 그룹 일정만 조회한다 (filterType=그룹명)")
        void success_getSpecificGroupSchedules() {
            // given
            Group group1 = Group.builder().id("group-1").build();
            Group group2 = Group.builder().id("group-2").build();
            Account account = createAccount("user@example.com", Arrays.asList(group1, group2));

            fakeScheduleStoragePort.database.add(createGroupSchedule(1L, "owner@example.com",
                "group-1", "그룹1 회의", LocalDateTime.of(2025, 1, 15, 10, 0)));
            fakeScheduleStoragePort.database.add(createGroupSchedule(2L, "owner@example.com",
                "group-2", "그룹2 회의", LocalDateTime.of(2025, 1, 15, 14, 0)));

            // 속하지 않은 그룹의 일정
            fakeScheduleStoragePort.database.add(createGroupSchedule(3L, "owner@example.com",
                "group-3", "그룹3 회의", LocalDateTime.of(2025, 1, 15, 16, 0)));

            // 개인 일정
            fakeScheduleStoragePort.database.add(createPersonalSchedule(4L, "user@example.com",
                "개인 회의", LocalDateTime.of(2025, 1, 15, 18, 0)));

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .filterType("group-1")
                .build();

            // when
            FindSchedulesServiceResponse response = findSchedulesService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(1);
            assertThat(response.schedules().get(0).content()).isEqualTo("그룹1 회의");
        }

        @Test
        @DisplayName("[success] 개인 + 그룹 일정을 모두 조회한다 (filterType=null)")
        void success_getAllSchedules() {
            // given
            Group group = Group.builder().id("group-1").build();
            Account account = createAccount("user@example.com", Arrays.asList(group));

            fakeScheduleStoragePort.database.add(createPersonalSchedule(1L, "user@example.com",
                "개인 회의 1", LocalDateTime.of(2025, 1, 15, 9, 0)));
            fakeScheduleStoragePort.database.add(createGroupSchedule(2L, "owner@example.com",
                "group-1", "그룹 회의 1", LocalDateTime.of(2025, 1, 15, 10, 0)));
            fakeScheduleStoragePort.database.add(createPersonalSchedule(3L, "user@example.com",
                "개인 회의 2", LocalDateTime.of(2025, 1, 15, 14, 0)));
            fakeScheduleStoragePort.database.add(createGroupSchedule(4L, "owner@example.com",
                "group-1", "그룹 회의 2", LocalDateTime.of(2025, 1, 15, 16, 0)));

            // 다른 사용자의 개인 일정
            fakeScheduleStoragePort.database.add(createPersonalSchedule(5L, "other@example.com",
                "다른 사용자 회의", LocalDateTime.of(2025, 1, 15, 18, 0)));

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .filterType(null)  // null이면 전체 조회
                .build();

            // when
            FindSchedulesServiceResponse response = findSchedulesService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(4);
            assertThat(response.schedules().get(0).content()).isEqualTo("개인 회의 1");
            assertThat(response.schedules().get(1).content()).isEqualTo("그룹 회의 1");
            assertThat(response.schedules().get(2).content()).isEqualTo("개인 회의 2");
            assertThat(response.schedules().get(3).content()).isEqualTo("그룹 회의 2");
        }

        @Test
        @DisplayName("[success] 조건에 맞는 일정이 없으면 빈 목록을 반환한다")
        void success_returnEmptyList() {
            // given
            Account account = createAccount("user@example.com", Arrays.asList());

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .filterType("PRIVATE")
                .build();

            // when
            FindSchedulesServiceResponse response = findSchedulesService.findSchedules(command);

            // then
            assertThat(response.schedules()).isEmpty();
        }
    }

    private Account createAccount(String email, List<Group> groups) {
        return Account.builder()
            .id(1L)
            .email(email)
            .nickname("User")
            .name("사용자")
            .groups(groups)
            .build();
    }

    private Schedule createPersonalSchedule(Long id, String email, String content,
        LocalDateTime startDt) {
        return Schedule.builder()
            .id(id)
            .writerEmail(email)
            .groupId(null)
            .content(content)
            .startDt(startDt)
            .endDt(startDt.plusHours(1))
            .notificationYn("N")
            .regDt(LocalDateTime.now())
            .build();
    }

    private Schedule createGroupSchedule(Long id, String email, String groupId, String content,
        LocalDateTime startDt) {
        return Schedule.builder()
            .id(id)
            .writerEmail(email)
            .groupId(groupId)
            .content(content)
            .startDt(startDt)
            .endDt(startDt.plusHours(1))
            .notificationYn("N")
            .regDt(LocalDateTime.now())
            .build();
    }

    private Schedule createScheduleWithCustomEndDate(Long id, String email, String content,
        LocalDateTime startDt, LocalDateTime endDt) {
        return Schedule.builder()
            .id(id)
            .writerEmail(email)
            .groupId(null)
            .content(content)
            .startDt(startDt)
            .endDt(endDt)
            .notificationYn("N")
            .regDt(LocalDateTime.now())
            .build();
    }
}
