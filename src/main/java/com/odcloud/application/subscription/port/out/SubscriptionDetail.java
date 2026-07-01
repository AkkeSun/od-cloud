package com.odcloud.application.subscription.port.out;

import java.time.LocalDate;

public record SubscriptionDetail(
    String groupName,
    String productName,
    String buyerNickname,
    LocalDate nextBillingDate
) {

}
