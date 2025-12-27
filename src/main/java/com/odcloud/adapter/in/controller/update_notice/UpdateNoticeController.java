package com.odcloud.adapter.in.controller.update_notice;

import com.odcloud.application.port.in.UpdateNoticeUseCase;
import com.odcloud.application.port.in.command.UpdateNoticeCommand;
import com.odcloud.application.service.update_notice.UpdateNoticeServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateNoticeController {

    private final UpdateNoticeUseCase useCase;

    @PutMapping("/groups/{groupId}/notices/{noticeId}")
    ApiResponse<UpdateNoticeResponse> update(
        @PathVariable String groupId,
        @PathVariable Long noticeId,
        @LoginAccount Account account,
        @RequestBody @Valid UpdateNoticeRequest request
    ) {
        UpdateNoticeCommand command = UpdateNoticeCommand.builder()
            .groupId(groupId)
            .noticeId(noticeId)
            .account(account)
            .title(request.title())
            .content(request.content())
            .build();

        UpdateNoticeServiceResponse serviceResponse = useCase.update(command);
        return ApiResponse.ok(UpdateNoticeResponse.of(serviceResponse));
    }
}
