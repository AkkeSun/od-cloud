package com.odcloud.application.schedule.port.out;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Schedule;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleStoragePort {

    void save(Schedule schedule);

    Schedule findById(Long scheduleId);

    void delete(Schedule schedule);

    List<Schedule> findSchedules(FindSchedulesCommand command);

    List<Schedule> findSchedulesForNotification(LocalDateTime currentTime);

    void updateNotificationYn(List<Long> scheduleIds);

    List<Schedule> findByPersonalSchedules(String writerEmail);

    List<Schedule> findByGroupId(Long groupId);
}
