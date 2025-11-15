package com.odcloud.adapter.in.update_schedule;

import com.odcloud.application.port.in.UpdateScheduleUseCase;
import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class UpdateScheduleController {

    private final UpdateScheduleUseCase useCase;

    @PutMapping("/schedules/{scheduleId}")
    ApiResponse<UpdateScheduleResponse> update(
        @PathVariable Long scheduleId,
        @RequestParam Long accountId,
        @Valid @RequestBody UpdateScheduleRequest request
    ) {
        UpdateScheduleServiceResponse response = useCase.update(
            request.toCommand(scheduleId, accountId));
        return ApiResponse.ok(UpdateScheduleResponse.of(response));
    }
}
