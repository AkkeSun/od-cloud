package com.odcloud.adapter.in.find_schedule;

import com.odcloud.application.port.in.FindScheduleUseCase;
import com.odcloud.application.service.find_schedule.FindScheduleServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class FindScheduleController {

    private final FindScheduleUseCase useCase;

    @GetMapping("/schedules")
    ApiResponse<FindScheduleResponse> findSchedules(@Valid FindScheduleRequest request) {
        FindScheduleServiceResponse response = useCase.findSchedules(request.toCommand());
        return ApiResponse.ok(FindScheduleResponse.of(response));
    }
}
