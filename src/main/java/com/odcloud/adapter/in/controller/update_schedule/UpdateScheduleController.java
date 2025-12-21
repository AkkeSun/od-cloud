package com.odcloud.adapter.in.controller.update_schedule;

import com.odcloud.application.port.in.UpdateScheduleUseCase;
import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import com.odcloud.resolver.LoginAccount;
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
