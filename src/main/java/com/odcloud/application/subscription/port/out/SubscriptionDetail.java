package com.odcloud.application.subscription.port.out;

import java.time.LocalDate;

public record SubscriptionDetail(
    Long productId,
    String productName,
    Long subscriptionId,
    Long groupId,
    String groupName,
    String buyerNickname,
    String status,
    LocalDate expiredDate
) {

}
