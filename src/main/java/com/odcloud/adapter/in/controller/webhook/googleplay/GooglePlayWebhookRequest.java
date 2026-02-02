package com.odcloud.adapter.in.controller.webhook.googleplay;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
import com.odcloud.domain.model.GooglePlayNotificationType;
import com.odcloud.infrastructure.util.StringUtil;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record GooglePlayWebhookRequest(
    @JsonProperty("message")
    Message message,

    @JsonProperty("subscription")
    String subscription
) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public record Message(
        @JsonProperty("data")
        String data,

        @JsonProperty("messageId")
        String messageId,

        @JsonProperty("publishTime")
        String publishTime
    ) {
    }

    GooglePlayNotificationCommand toCommand() {
        if (message == null || message.data() == null) {
            throw new IllegalArgumentException("Invalid Google Play webhook request: missing message data");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(message.data());
            String jsonData = new String(decodedBytes, StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(jsonData);

            String packageName = getTextOrNull(root, "packageName");
            Long eventTimeMillis = getLongOrNull(root, "eventTimeMillis");

            // Test notification
            if (root.has("testNotification")) {
                return new GooglePlayNotificationCommand(
                    packageName, eventTimeMillis, null, null, null, null, null, true
                );
            }

            // Subscription notification
            if (root.has("subscriptionNotification")) {
                JsonNode sub = root.get("subscriptionNotification");
                int typeCode = sub.get("notificationType").asInt();
                return new GooglePlayNotificationCommand(
                    packageName,
                    eventTimeMillis,
                    GooglePlayNotificationType.fromSubscriptionCode(typeCode),
                    getTextOrNull(sub, "purchaseToken"),
                    getTextOrNull(sub, "subscriptionId"),
                    null,
                    null,
                    false
                );
            }

            // Voided purchase notification
            if (root.has("voidedPurchaseNotification")) {
                JsonNode voided = root.get("voidedPurchaseNotification");
                return new GooglePlayNotificationCommand(
                    packageName,
                    eventTimeMillis,
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    getTextOrNull(voided, "purchaseToken"),
                    null,
                    getTextOrNull(voided, "orderId"),
                    getIntOrNull(voided, "refundType"),
                    false
                );
            }

            // One-time product notification (not processed)
            if (root.has("oneTimeProductNotification")) {
                JsonNode oneTime = root.get("oneTimeProductNotification");
                int typeCode = oneTime.get("notificationType").asInt();
                return new GooglePlayNotificationCommand(
                    packageName,
                    eventTimeMillis,
                    GooglePlayNotificationType.fromOneTimeCode(typeCode),
                    getTextOrNull(oneTime, "purchaseToken"),
                    getTextOrNull(oneTime, "sku"),
                    null,
                    null,
                    false
                );
            }

            throw new IllegalArgumentException("Unknown Google Play notification type");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode Google Play notification data", e);
        }
    }

    private static String getTextOrNull(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }

    private static Long getLongOrNull(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asLong() : null;
    }

    private static Integer getIntOrNull(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asInt() : null;
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
