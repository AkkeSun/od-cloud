package com.odcloud.application.webhook.port.in;

import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;

public interface HandleGooglePlayWebhookUseCase {

    void handle(GooglePlayNotificationCommand command);
}
