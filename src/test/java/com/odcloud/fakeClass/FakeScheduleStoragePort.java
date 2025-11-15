package com.odcloud.fakeClass;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeScheduleStoragePort implements ScheduleStoragePort {

    public List<Schedule> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void save(Schedule schedule) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Schedule savedSchedule = Schedule.builder()
            .id(schedule.getId() == null ? ++id : schedule.getId())
            .title(schedule.getTitle())
            .description(schedule.getDescription())
            .startDt(schedule.getStartDt())
            .endDt(schedule.getEndDt())
            .scheduleType(schedule.getScheduleType())
            .accountId(schedule.getAccountId())
            .groupId(schedule.getGroupId())
            .notificationEnabled(schedule.getNotificationEnabled())
            .notificationMinutes(schedule.getNotificationMinutes())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();

        database.removeIf(s -> s.getId().equals(savedSchedule.getId()));
        database.add(savedSchedule);
        log.info("FakeScheduleStoragePort saved schedule: id={}, title={}", savedSchedule.getId(),
            savedSchedule.getTitle());
    }

    @Override
    public Schedule findById(Long id) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .filter(schedule -> schedule.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public List<Schedule> findByConditions(Long accountId, ScheduleType scheduleType,
        LocalDateTime startDate, LocalDateTime endDate, List<Long> groupIds) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        return database.stream()
            .filter(schedule -> accountId == null || schedule.getAccountId().equals(accountId))
            .filter(schedule -> scheduleType == null || schedule.getScheduleType()
                .equals(scheduleType))
            .filter(schedule -> {
                if (startDate == null || endDate == null) {
                    return true;
                }
                return schedule.getStartDt().isBefore(endDate)
                    && schedule.getEndDt().isAfter(startDate);
            })
            .filter(schedule -> {
                if (groupIds == null || groupIds.isEmpty()) {
                    return true;
                }
                return schedule.getGroupId() != null && groupIds.contains(schedule.getGroupId());
            })
            .sorted((a, b) -> a.getStartDt().compareTo(b.getStartDt()))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        database.removeIf(schedule -> schedule.getId().equals(id));
        log.info("FakeScheduleStoragePort deleted schedule: id={}", id);
    }

    @Override
    public boolean existsByIdAndAccountId(Long id, Long accountId) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        return database.stream()
            .anyMatch(schedule -> schedule.getId().equals(id)
                && schedule.getAccountId().equals(accountId));
    }

    public void reset() {
        database.clear();
        id = 0L;
        shouldThrowException = false;
    }
}
