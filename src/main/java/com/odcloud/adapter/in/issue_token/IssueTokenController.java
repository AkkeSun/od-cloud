package com.odcloud.adapter.in.issue_token;

import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTokenController {

    private final IssueTokenUseCase useCase;

    @PostMapping("/auth/2fa")
    ApiResponse<IssueTokenResponse> issue(
        @LoginAccount Account account,
        @RequestBody @Valid IssueTokenRequest request
    ) {
        IssueTokenServiceResponse serviceResponse = useCase.issue(request.toCommand(account));

        return ApiResponse.ok(IssueTokenResponse.of(serviceResponse));
    }
}
