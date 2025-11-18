package com.odcloud.application.port.out;

import com.odcloud.domain.model.Schedule;

public interface ScheduleStoragePort {

    void save(Schedule schedule);

    Schedule findById(Long scheduleId);

    void delete(Schedule schedule);
}
