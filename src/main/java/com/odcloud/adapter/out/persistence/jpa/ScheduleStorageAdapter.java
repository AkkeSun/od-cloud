package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SCHEDULE;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.util.AesUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ScheduleStorageAdapter implements ScheduleStoragePort {

    private final ScheduleRepository repository;
    private final AesUtil aesUtil;

    @Override
    public void save(Schedule schedule) {
        repository.save(encryptSchedule(schedule));
    }

    @Override
    public Schedule findById(Long scheduleId) {
        ScheduleEntity entity = repository.findById(scheduleId);
        if (entity == null) {
            throw new CustomBusinessException(Business_NOT_FOUND_SCHEDULE);
        }
        return decryptSchedule(entity.toDomain());
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
            .map(this::decryptSchedule)
            .toList();
    }

    @Override
    public List<Schedule> findSchedulesForNotification(LocalDateTime currentTime) {
        return repository.findSchedulesForNotification(currentTime)
            .stream()
            .map(ScheduleEntity::toDomain)
            .map(this::decryptSchedule)
            .toList();
    }

    @Override
    public void updateNotificationYn(List<Long> scheduleIds) {
        repository.updateNotificationYn(scheduleIds);
    }

    @Override
    public List<Schedule> findByPersonalSchedules(String writerEmail) {
        return repository.findByPersonalSchedules(writerEmail)
            .stream()
            .map(ScheduleEntity::toDomain)
            .map(this::decryptSchedule)
            .toList();
    }

    @Override
    public List<Schedule> findByGroupId(Long groupId) {
        return repository.findByGroupId(groupId)
            .stream()
            .map(ScheduleEntity::toDomain)
            .map(this::decryptSchedule)
            .toList();
    }

    private Schedule encryptSchedule(Schedule schedule) {
        return Schedule.builder()
            .id(schedule.getId())
            .writerEmail(schedule.getWriterEmail())
            .groupId(schedule.getGroupId())
            .content(aesUtil.encryptText(schedule.getContent()))
            .notificationDt(schedule.getNotificationDt())
            .notificationYn(schedule.getNotificationYn())
            .startDt(schedule.getStartDt())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();
    }

    private Schedule decryptSchedule(Schedule schedule) {
        return Schedule.builder()
            .id(schedule.getId())
            .writerEmail(schedule.getWriterEmail())
            .groupId(schedule.getGroupId())
            .content(aesUtil.decryptText(schedule.getContent()))
            .notificationDt(schedule.getNotificationDt())
            .notificationYn(schedule.getNotificationYn())
            .startDt(schedule.getStartDt())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();
    }
}
