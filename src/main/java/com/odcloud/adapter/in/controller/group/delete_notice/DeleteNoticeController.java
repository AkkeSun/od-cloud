package com.odcloud.adapter.in.controller.group.delete_notice;

import com.odcloud.application.group.port.in.DeleteNoticeUseCase;
import com.odcloud.application.group.service.delete_notice.DeleteNoticeResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
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
        @PathVariable Long groupId,
        @PathVariable Long noticeId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.delete(new DeleteNoticeRequest().toCommand(groupId, noticeId, account)));
    }
}
