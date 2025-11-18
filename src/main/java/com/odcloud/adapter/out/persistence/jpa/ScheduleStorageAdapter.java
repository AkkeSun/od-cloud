package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ScheduleStorageAdapter implements ScheduleStoragePort {

    private final ScheduleRepository repository;

    @Override
    public void save(Schedule schedule) {
        repository.save(schedule);
    }
}
