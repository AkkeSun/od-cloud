package com.odcloud.application.port.out;

import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleStoragePort {

    void save(Schedule schedule);

    Schedule findById(Long id);

    List<Schedule> findByConditions(Long accountId, ScheduleType scheduleType,
        LocalDateTime startDate, LocalDateTime endDate, List<Long> groupIds);

    void deleteById(Long id);

    boolean existsByIdAndAccountId(Long id, Long accountId);
}
