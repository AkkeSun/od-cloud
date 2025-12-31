package com.odcloud.application.schedule.service.find_schedules;


import com.odcloud.application.schedule.port.in.FindSchedulesUseCase;
import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
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
