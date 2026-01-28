package com.odcloud.adapter.out.client.payment;

record AppleVerifyReceiptResponse(
    int status,
    String environment
) {

    boolean isValid() {
        return status == 0;
    }
}
