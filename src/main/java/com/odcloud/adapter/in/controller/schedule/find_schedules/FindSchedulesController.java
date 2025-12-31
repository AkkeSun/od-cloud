package com.odcloud.adapter.in.controller.schedule.find_schedules;

import com.odcloud.application.schedule.port.in.FindSchedulesUseCase;
import com.odcloud.application.schedule.service.find_schedules.FindSchedulesServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindSchedulesController {

    private final FindSchedulesUseCase useCase;

    @GetMapping("/schedules")
    ApiResponse<FindSchedulesResponse> findSchedules(
        @Validated(ValidationSequence.class) FindSchedulesRequest request,
        @LoginAccount Account account
    ) {
        FindSchedulesServiceResponse response = useCase.findSchedules(request.toCommand(account));
        return ApiResponse.ok(FindSchedulesResponse.of(response));
    }
}
