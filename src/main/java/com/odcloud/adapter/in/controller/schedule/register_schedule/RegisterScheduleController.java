package com.odcloud.adapter.in.controller.schedule.register_schedule;

import com.odcloud.application.schedule.port.in.RegisterSchedulerUseCase;
import com.odcloud.application.schedule.service.register_schedule.RegisterScheduleServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterScheduleController {

    private final RegisterSchedulerUseCase useCase;

    @PostMapping("/schedules")
    ApiResponse<RegisterScheduleResponse> register(
        @Validated(ValidationSequence.class) @RequestBody RegisterScheduleRequest request,
        @LoginAccount Account account
    ) {
        RegisterScheduleServiceResponse response = useCase.register(request.toCommand(account));
        return ApiResponse.ok(RegisterScheduleResponse.of(response));
    }
}
