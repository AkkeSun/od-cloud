package com.odcloud.application.subscription.port.out;

public record SubscriptionDetail(
    Long groupId,
    String groupName,
    String productName,
    Long buyerId,
    String buyerNickname,
    String status
) {

}
