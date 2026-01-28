package com.odcloud.adapter.out.client.payment;

record GooglePlayVerifyPurchaseResponse(
    String kind,
    Long purchaseTimeMillis,
    Integer purchaseState,
    Integer consumptionState,
    String developerPayload,
    String orderId,
    Integer purchaseType,
    Integer acknowledgementState,
    String purchaseToken,
    String productId,
    Integer quantity,
    String obfuscatedExternalAccountId,
    String obfuscatedExternalProfileId,
    String regionCode
) {

    boolean isValid() {
        // purchaseState: 0 = Purchased, 1 = Canceled, 2 = Pending
        return purchaseState != null && purchaseState == 0;
    }
}
