package com.odcloud.application.service.find_schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindScheduleCommand;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.SchedulePeriod;
import com.odcloud.domain.model.ScheduleType;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindScheduleServiceTest {

    private FindScheduleService findScheduleService;
    private FakeScheduleStoragePort fakeScheduleStoragePort;

    @BeforeEach
    void setUp() {
        fakeScheduleStoragePort = new FakeScheduleStoragePort();
        findScheduleService = new FindScheduleService(fakeScheduleStoragePort);
    }

    @Nested
    @DisplayName("[findSchedules] 일정 조회")
    class Describe_findSchedules {

        @Test
        @DisplayName("[success] 일간 일정을 조회한다")
        void success_dailySchedule() {
            // given
            Schedule schedule1 = Schedule.createPersonal(
                "아침 미팅", null,
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0),
                1L, false, null
            );
            Schedule schedule2 = Schedule.createPersonal(
                "점심 약속", null,
                LocalDateTime.of(2025, 1, 15, 12, 0),
                LocalDateTime.of(2025, 1, 15, 13, 0),
                1L, false, null
            );
            Schedule schedule3 = Schedule.createPersonal(
                "다음날 일정", null,
                LocalDateTime.of(2025, 1, 16, 9, 0),
                LocalDateTime.of(2025, 1, 16, 10, 0),
                1L, false, null
            );

            fakeScheduleStoragePort.save(schedule1);
            fakeScheduleStoragePort.save(schedule2);
            fakeScheduleStoragePort.save(schedule3);

            FindScheduleCommand command = FindScheduleCommand.builder()
                .accountId(1L)
                .period(SchedulePeriod.DAILY)
                .baseDate(LocalDate.of(2025, 1, 15))
                .scheduleType(null)
                .groupIds(null)
                .build();

            // when
            FindScheduleServiceResponse response = findScheduleService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(2);
            assertThat(response.schedules()).extracting("title")
                .containsExactly("아침 미팅", "점심 약속");
        }

        @Test
        @DisplayName("[success] 주간 일정을 조회한다")
        void success_weeklySchedule() {
            // given
            // 2025-01-13 (월) ~ 2025-01-19 (일)
            Schedule monday = Schedule.createPersonal(
                "월요일 일정", null,
                LocalDateTime.of(2025, 1, 13, 9, 0),
                LocalDateTime.of(2025, 1, 13, 10, 0),
                1L, false, null
            );
            Schedule friday = Schedule.createPersonal(
                "금요일 일정", null,
                LocalDateTime.of(2025, 1, 17, 9, 0),
                LocalDateTime.of(2025, 1, 17, 10, 0),
                1L, false, null
            );
            Schedule nextWeek = Schedule.createPersonal(
                "다음주 일정", null,
                LocalDateTime.of(2025, 1, 20, 9, 0),
                LocalDateTime.of(2025, 1, 20, 10, 0),
                1L, false, null
            );

            fakeScheduleStoragePort.save(monday);
            fakeScheduleStoragePort.save(friday);
            fakeScheduleStoragePort.save(nextWeek);

            FindScheduleCommand command = FindScheduleCommand.builder()
                .accountId(1L)
                .period(SchedulePeriod.WEEKLY)
                .baseDate(LocalDate.of(2025, 1, 15))  // 수요일
                .scheduleType(null)
                .groupIds(null)
                .build();

            // when
            FindScheduleServiceResponse response = findScheduleService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(2);
            assertThat(response.schedules()).extracting("title")
                .containsExactly("월요일 일정", "금요일 일정");
        }

        @Test
        @DisplayName("[success] 개인 일정만 조회한다")
        void success_personalSchedulesOnly() {
            // given
            Schedule personal = Schedule.createPersonal(
                "개인 일정", null,
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0),
                1L, false, null
            );
            Schedule group = Schedule.createGroup(
                "그룹 일정", null,
                LocalDateTime.of(2025, 1, 15, 14, 0),
                LocalDateTime.of(2025, 1, 15, 15, 0),
                1L, 10L, false, null
            );

            fakeScheduleStoragePort.save(personal);
            fakeScheduleStoragePort.save(group);

            FindScheduleCommand command = FindScheduleCommand.builder()
                .accountId(1L)
                .period(SchedulePeriod.DAILY)
                .baseDate(LocalDate.of(2025, 1, 15))
                .scheduleType(ScheduleType.PERSONAL)
                .groupIds(null)
                .build();

            // when
            FindScheduleServiceResponse response = findScheduleService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(1);
            assertThat(response.schedules().get(0).getTitle()).isEqualTo("개인 일정");
        }

        @Test
        @DisplayName("[success] 그룹 일정만 조회한다")
        void success_groupSchedulesOnly() {
            // given
            Schedule personal = Schedule.createPersonal(
                "개인 일정", null,
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0),
                1L, false, null
            );
            Schedule group = Schedule.createGroup(
                "그룹 일정", null,
                LocalDateTime.of(2025, 1, 15, 14, 0),
                LocalDateTime.of(2025, 1, 15, 15, 0),
                1L, 10L, false, null
            );

            fakeScheduleStoragePort.save(personal);
            fakeScheduleStoragePort.save(group);

            FindScheduleCommand command = FindScheduleCommand.builder()
                .accountId(1L)
                .period(SchedulePeriod.DAILY)
                .baseDate(LocalDate.of(2025, 1, 15))
                .scheduleType(ScheduleType.GROUP)
                .groupIds(List.of(10L))
                .build();

            // when
            FindScheduleServiceResponse response = findScheduleService.findSchedules(command);

            // then
            assertThat(response.schedules()).hasSize(1);
            assertThat(response.schedules().get(0).getTitle()).isEqualTo("그룹 일정");
        }
    }
}
