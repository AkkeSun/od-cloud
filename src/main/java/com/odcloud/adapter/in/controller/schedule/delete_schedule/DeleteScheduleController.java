package com.odcloud.adapter.in.controller.schedule.delete_schedule;

import com.odcloud.application.schedule.port.in.DeleteScheduleUseCase;
import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteScheduleController {

    private final DeleteScheduleUseCase useCase;

    @DeleteMapping("/schedules/{scheduleId}")
    ApiResponse<DeleteScheduleResponse> delete(
        @PathVariable Long scheduleId,
        @LoginAccount Account account
    ) {
        DeleteScheduleServiceResponse response = useCase.delete(scheduleId, account);
        return ApiResponse.ok(DeleteScheduleResponse.of(response));
    }
}
