package com.odcloud.adapter.out.client.apple;

public record AppleVerifyReceiptResponse(
    int status,
    String environment
) {

    public boolean isValid() {
        return status == 0;
    }
}
