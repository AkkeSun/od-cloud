package com.odcloud.application.webhook.port.in.command;

import com.odcloud.domain.model.GooglePlayNotificationType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Builder;

@Builder
public record GooglePlayNotificationCommand(
    String packageName,
    Long eventTimeMillis,
    GooglePlayNotificationType notificationType,
    String purchaseToken,
    String subscriptionId,
    String orderId,
    Integer refundType,
    boolean testNotification
) {

    public LocalDateTime storeProcessDt() {
        return Instant
            .ofEpochMilli(eventTimeMillis)
            .atZone(ZoneId.of("Asia/Seoul"))
            .toLocalDateTime();
    }

    public boolean invalidRequest() {
        return packageName == null;
    }
}
