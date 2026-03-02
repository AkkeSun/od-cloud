package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import com.odcloud.application.auth.port.in.CallbackGoogleOAuth2UseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CallbackGoogleOAuth2Controller {

    private final CallbackGoogleOAuth2UseCase useCase;

    @GetMapping("/auth/google")
    ResponseEntity<String> callback(@Valid CallbackGoogleOAuth2Request request) {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(useCase.callback(request.getCode()));
    }
}
