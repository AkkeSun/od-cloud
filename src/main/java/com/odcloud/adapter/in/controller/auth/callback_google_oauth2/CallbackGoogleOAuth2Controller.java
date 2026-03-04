package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import com.odcloud.application.auth.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2Response;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CallbackGoogleOAuth2Controller {

    private final CallbackGoogleOAuth2UseCase useCase;

    @GetMapping("/auth/google")
    ResponseEntity<Void> callback(@Valid CallbackGoogleOAuth2Request request) {
        CallbackGoogleOAuth2Response response = useCase.callback(request.getCode());
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(response.redirectUri()))
            .build();
    }
}
