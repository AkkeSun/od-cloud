package com.odcloud.adapter.in.controller.webhook.googleplay;

import com.odcloud.application.webhook.port.in.HandleGooglePlayWebhookUseCase;
import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
class GooglePlayWebhookController {

    private final HandleGooglePlayWebhookUseCase useCase;

    @PostMapping("/webhook/googleplay")
    ResponseEntity<Void> handleGooglePlayWebhook(@RequestBody GooglePlayWebhookRequest request) {
        GooglePlayNotificationCommand command = request.toCommand();
        if (command.invalidRequest()) {
            return ResponseEntity.ok().build();
        }

        useCase.handle(command);
        return ResponseEntity.ok().build();
    }
}
