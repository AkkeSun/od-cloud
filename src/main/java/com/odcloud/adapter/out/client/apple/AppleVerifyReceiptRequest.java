package com.odcloud.adapter.out.client.apple;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleVerifyReceiptRequest(
    @JsonProperty("receipt-data")
    String receiptData,
    String password,
    @JsonProperty("exclude-old-transactions")
    boolean excludeOldTransactions
) {

    public static AppleVerifyReceiptRequest of(String receiptData, String password) {
        return new AppleVerifyReceiptRequest(receiptData, password, true);
    }
}
