package com.odcloud.adapter.in.delete_schedule;

import com.odcloud.application.port.in.DeleteScheduleUseCase;
import com.odcloud.application.port.in.command.DeleteScheduleCommand;
import com.odcloud.application.service.delete_schedule.DeleteScheduleServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteScheduleController {

    private final DeleteScheduleUseCase useCase;

    @DeleteMapping("/schedules/{scheduleId}")
    ApiResponse<DeleteScheduleResponse> delete(
        @PathVariable Long scheduleId,
        @RequestParam Long accountId
    ) {
        DeleteScheduleCommand command = DeleteScheduleCommand.builder()
            .scheduleId(scheduleId)
            .accountId(accountId)
            .build();

        DeleteScheduleServiceResponse response = useCase.delete(command);
        return ApiResponse.ok(DeleteScheduleResponse.of(response));
    }
}
