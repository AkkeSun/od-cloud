package com.odcloud.application.port.out;

import com.odcloud.domain.model.Schedule;

public interface ScheduleStoragePort {

    void save(Schedule schedule);
}
