package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ScheduleStorageAdapter implements ScheduleStoragePort {

    private final ScheduleRepository scheduleRepository;

    @Override
    public void save(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public List<Schedule> findByConditions(Long accountId, ScheduleType scheduleType,
        LocalDateTime startDate, LocalDateTime endDate, List<Long> groupIds) {
        return scheduleRepository.findByConditions(accountId, scheduleType, startDate, endDate,
            groupIds);
    }

    @Override
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdAndAccountId(Long id, Long accountId) {
        return scheduleRepository.existsByIdAndAccountId(id, accountId);
    }
}
