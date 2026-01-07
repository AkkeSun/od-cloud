package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SCHEDULE;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<Schedule> findSchedules(FindSchedulesCommand command) {
        return repository.findSchedules(command)
            .stream()
            .map(ScheduleEntity::toDomain)
            .toList();
    }

    @Override
    public List<Schedule> findSchedulesForNotification(LocalDateTime currentTime) {
        return repository.findSchedulesForNotification(currentTime)
            .stream()
            .map(ScheduleEntity::toDomain)
            .toList();
    }

    @Override
    public void updateNotificationYn(List<Long> scheduleIds) {
        repository.updateNotificationYn(scheduleIds);
    }

    @Override
    public List<Schedule> findByWriterEmailAndGroupIdIsNull(String writerEmail) {
        return repository.findByWriterEmailAndGroupIdIsNull(writerEmail)
            .stream()
            .map(ScheduleEntity::toDomain)
            .toList();
    }

    @Override
    public List<Schedule> findByGroupId(Long groupId) {
        return repository.findByGroupId(groupId)
            .stream()
            .map(ScheduleEntity::toDomain)
            .toList();
    }
}
