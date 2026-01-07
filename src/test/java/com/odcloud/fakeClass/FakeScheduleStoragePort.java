package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SCHEDULE;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FakeScheduleStoragePort implements ScheduleStoragePort {

    public List<Schedule> database = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public void save(Schedule schedule) {
        if (schedule.getId() == null) {
            Schedule withId = Schedule.builder()
                .id(nextId++)
                .writerEmail(schedule.getWriterEmail())
                .groupId(schedule.getGroupId())
                .content(schedule.getContent())
                .notificationDt(schedule.getNotificationDt())
                .notificationYn(schedule.getNotificationYn())
                .startDt(schedule.getStartDt())
                .modDt(schedule.getModDt())
                .regDt(schedule.getRegDt())
                .build();
            database.add(withId);
        } else {
            database.add(schedule);
        }
    }

    @Override
    public Schedule findById(Long scheduleId) {
        return database.stream()
            .filter(s -> s.getId().equals(scheduleId))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_SCHEDULE));
    }

    @Override
    public void delete(Schedule schedule) {
        database.removeIf(s -> s.getId().equals(schedule.getId()));
    }

    @Override
    public List<Schedule> findSchedules(FindSchedulesCommand command) {
        return database.stream()
            // 일정 기간과 조회 범위가 겹치는 모든 일정 조회
            .filter(s -> !s.getStartDt().isBefore(command.getStartDateTime())
                && !s.getStartDt().isAfter(command.getEndDateTime()))
            .filter(s -> matchesFilterType(s, command))
            .sorted((s1, s2) -> s1.getStartDt().compareTo(s2.getStartDt()))
            .toList();
    }

    @Override
    public List<Schedule> findSchedulesForNotification(LocalDateTime currentTime) {
        return database.stream()
            .filter(s -> s.getNotificationDt() != null)
            .filter(s -> !s.getNotificationDt().isAfter(currentTime))
            .filter(s -> "N".equals(s.getNotificationYn()))
            .toList();
    }

    @Override
    public void updateNotificationYn(List<Long> scheduleIds) {
        for (Long scheduleId : scheduleIds) {
            for (int i = 0; i < database.size(); i++) {
                Schedule s = database.get(i);
                if (scheduleId.equals(s.getId())) {
                    Schedule updated = Schedule.builder()
                        .id(s.getId())
                        .writerEmail(s.getWriterEmail())
                        .groupId(s.getGroupId())
                        .content(s.getContent())
                        .notificationDt(s.getNotificationDt())
                        .notificationYn("Y")
                        .startDt(s.getStartDt())
                        .modDt(LocalDateTime.now())
                        .regDt(s.getRegDt())
                        .build();
                    database.set(i, updated);
                    break;
                }
            }
        }
    }

    private boolean matchesFilterType(Schedule schedule, FindSchedulesCommand command) {
        String email = command.account().getEmail();
        List<Long> groupIds = command.account().getGroupIds();
        Long groupId = command.groupId();

        if (groupId == null) {
            return (schedule.getGroupId() == null && email.equals(schedule.getWriterEmail()))
                || (schedule.getGroupId() != null && groupIds.contains(schedule.getGroupId()));
        }

        if (groupId == 0L) {
            return schedule.getGroupId() == null && email.equals(schedule.getWriterEmail());
        }

        return groupId.equals(schedule.getGroupId());
    }

    @Override
    public List<Schedule> findByWriterEmailAndGroupIdIsNull(String writerEmail) {
        return database.stream()
            .filter(s -> s.getWriterEmail().equals(writerEmail))
            .filter(s -> s.getGroupId() == null)
            .toList();
    }

    @Override
    public List<Schedule> findByGroupId(Long groupId) {
        return database.stream()
            .filter(s -> groupId.equals(s.getGroupId()))
            .toList();
    }
}
