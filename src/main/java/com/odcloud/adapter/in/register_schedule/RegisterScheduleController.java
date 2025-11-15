package com.odcloud.adapter.in.register_schedule;

import com.odcloud.application.port.in.RegisterScheduleUseCase;
import com.odcloud.application.service.register_schedule.RegisterScheduleServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class RegisterScheduleController {

    private final RegisterScheduleUseCase useCase;

    @PostMapping("/schedules")
    ApiResponse<RegisterScheduleResponse> register(@Valid @RequestBody RegisterScheduleRequest request) {
        RegisterScheduleServiceResponse response = useCase.register(request.toCommand());
        return ApiResponse.ok(RegisterScheduleResponse.of(response));
    }
}
