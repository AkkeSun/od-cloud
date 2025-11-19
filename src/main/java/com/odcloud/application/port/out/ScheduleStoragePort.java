package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Schedule;
import java.util.List;

public interface ScheduleStoragePort {

    void save(Schedule schedule);

    Schedule findById(Long scheduleId);

    void delete(Schedule schedule);

    List<Schedule> findSchedules(FindSchedulesCommand command);
}
