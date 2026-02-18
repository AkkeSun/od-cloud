package com.odcloud.adapter.in.controller.auth.issue_token;

import com.odcloud.application.auth.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.auth.port.in.IssueTokenUseCase;
import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;
import com.odcloud.application.auth.service.issue_token.IssueTokenServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTokenController {

    private final IssueTokenUseCase useCase;
    private final CallbackGoogleOAuth2UseCase googleOAuth2UseCase;

    @PostMapping("/auth")
    ApiResponse<IssueTokenResponse> issue(@RequestHeader String googleAuthorization) {
        IssueTokenServiceResponse serviceResponse = useCase.issue(googleAuthorization);
        return ApiResponse.ok(IssueTokenResponse.of(serviceResponse));
    }

    @GetMapping("/auth/google")
    ApiResponse<IssueTokenResponse> issueForWeb(String code) {
        CallbackGoogleOAuth2ServiceResponse callback = googleOAuth2UseCase.callback(code);
        IssueTokenServiceResponse serviceResponse = useCase.issue(callback.googleAccessToken());
        return ApiResponse.ok(IssueTokenResponse.of(serviceResponse));
    }
}
