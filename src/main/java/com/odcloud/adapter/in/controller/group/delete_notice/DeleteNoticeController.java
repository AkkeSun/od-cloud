package com.odcloud.adapter.in.controller.group.delete_notice;

import com.odcloud.application.port.in.DeleteNoticeUseCase;
import com.odcloud.application.port.in.command.DeleteNoticeCommand;
import com.odcloud.application.service.delete_notice.DeleteNoticeServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteNoticeController {

    private final DeleteNoticeUseCase useCase;

    @DeleteMapping("/groups/{groupId}/notices/{noticeId}")
    ApiResponse<DeleteNoticeResponse> delete(
        @PathVariable String groupId,
        @PathVariable Long noticeId,
        @LoginAccount Account account
    ) {
        DeleteNoticeCommand command = DeleteNoticeCommand.builder()
            .groupId(groupId)
            .noticeId(noticeId)
            .account(account)
            .build();

        DeleteNoticeServiceResponse serviceResponse = useCase.delete(command);
        return ApiResponse.ok(DeleteNoticeResponse.of(serviceResponse));
    }
}
