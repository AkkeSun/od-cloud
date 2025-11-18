package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SCHEDULE;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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

    @Override
    public Schedule findById(Long scheduleId) {
        ScheduleEntity entity = repository.findById(scheduleId);
        if (entity == null) {
            throw new CustomBusinessException(Business_NOT_FOUND_SCHEDULE);
        }
        return entity.toDomain();
    }

    @Override
    public void delete(Schedule schedule) {
        repository.delete(schedule);
    }
}
