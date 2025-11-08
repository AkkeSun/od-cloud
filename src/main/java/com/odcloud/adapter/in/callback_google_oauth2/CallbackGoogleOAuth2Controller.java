package com.odcloud.adapter.in.callback_google_oauth2;

import com.odcloud.application.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CallbackGoogleOAuth2Controller {

    private final CallbackGoogleOAuth2UseCase useCase;

    @GetMapping("/auth/google")
    ApiResponse<CallbackGoogleOAuth2Response> callback(
        @Valid CallbackGoogleOAuth2Request request
    ) {
        CallbackGoogleOAuth2ServiceResponse serviceResponse = useCase.callback(request.getCode());
        return ApiResponse.ok(CallbackGoogleOAuth2Response.of(serviceResponse));
    }
}
