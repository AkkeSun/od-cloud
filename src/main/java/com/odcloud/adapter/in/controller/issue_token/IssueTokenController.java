package com.odcloud.adapter.in.controller.issue_token;

import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTokenController {

    private final IssueTokenUseCase useCase;

    @PostMapping("/auth")
    ApiResponse<IssueTokenResponse> issue(@RequestHeader String googleAuthorization) {
        IssueTokenServiceResponse serviceResponse = useCase.issue(googleAuthorization);
        return ApiResponse.ok(IssueTokenResponse.of(serviceResponse));
    }
}
