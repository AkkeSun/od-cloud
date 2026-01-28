package com.odcloud.adapter.out.client.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

record AppleVerifyReceiptRequest(
    @JsonProperty("receipt-data")
    String receiptData,
    String password,
    @JsonProperty("exclude-old-transactions")
    boolean excludeOldTransactions
) {

    static AppleVerifyReceiptRequest of(String receiptData, String password) {
        return new AppleVerifyReceiptRequest(receiptData, password, true);
    }
}
