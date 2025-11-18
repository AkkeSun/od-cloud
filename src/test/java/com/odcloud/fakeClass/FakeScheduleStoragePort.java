package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SCHEDULE;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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
                .endDt(schedule.getEndDt())
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
}
