package com.odcloud.fakeClass;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import java.util.ArrayList;
import java.util.List;

public class FakeScheduleStoragePort implements ScheduleStoragePort {

    public List<Schedule> database = new ArrayList<>();

    @Override
    public void save(Schedule schedule) {
        database.add(schedule);
    }
}
