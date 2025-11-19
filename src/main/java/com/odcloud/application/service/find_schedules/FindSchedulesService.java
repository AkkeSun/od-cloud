package com.odcloud.application.service.find_schedules;

import com.odcloud.application.port.in.FindSchedulesUseCase;
import com.odcloud.application.port.in.command.FindSchedulesCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindSchedulesService implements FindSchedulesUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    public FindSchedulesServiceResponse findSchedules(FindSchedulesCommand command) {
        List<Schedule> schedules = scheduleStoragePort.findSchedules(command);
        return FindSchedulesServiceResponse.of(schedules);
    }
}
