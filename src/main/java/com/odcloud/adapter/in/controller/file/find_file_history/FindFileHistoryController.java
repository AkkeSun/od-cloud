package com.odcloud.adapter.in.controller.file.find_file_history;

import com.odcloud.application.file.port.in.FindFileHistoryUseCase;
import com.odcloud.application.file.service.find_file_history.FindFileHistoryCommand;
import com.odcloud.application.file.service.find_file_history.FindFileHistoryResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindFileHistoryController {

    private final FindFileHistoryUseCase useCase;

    @GetMapping("/files/{fileId}/history")
    ApiResponse<FindFileHistoryResponse> findHistory(
        @PathVariable Long fileId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.findHistory(
            FindFileHistoryCommand.builder()
                .fileId(fileId)
                .account(account)
                .build()
        ));
    }
}
