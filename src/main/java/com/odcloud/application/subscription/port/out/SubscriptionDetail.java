package com.odcloud.application.subscription.port.out;

import java.time.LocalDateTime;

public record SubscriptionDetail(
    Long groupId,
    String groupName,
    String productName,
    Long buyerId,
    String buyerNickname,
    String status,
    LocalDateTime expiredDate
) {

}
