package com.odcloud.adapter.in.controller.schedule.update_schedule;

import com.odcloud.application.schedule.port.in.UpdateScheduleUseCase;
import com.odcloud.application.schedule.service.update_schedule.UpdateScheduleServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateScheduleController {

    private final UpdateScheduleUseCase useCase;

    @PatchMapping("/schedules/{scheduleId}")
    ApiResponse<UpdateScheduleResponse> update(
        @PathVariable Long scheduleId,
        @Validated(ValidationSequence.class) @RequestBody UpdateScheduleRequest request,
        @LoginAccount Account account
    ) {
        UpdateScheduleServiceResponse response = useCase.update(
            request.toCommand(scheduleId, account));
        return ApiResponse.ok(UpdateScheduleResponse.of(response));
    }
}
