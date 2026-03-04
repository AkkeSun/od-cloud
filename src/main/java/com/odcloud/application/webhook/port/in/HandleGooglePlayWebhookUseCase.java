package com.odcloud.application.webhook.port.in;

import com.odcloud.application.webhook.service.googleplay.GooglePlayNotificationCommand;

public interface HandleGooglePlayWebhookUseCase {

    void handle(GooglePlayNotificationCommand command);
}
