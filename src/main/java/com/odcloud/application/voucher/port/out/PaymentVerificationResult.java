package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.StoreType;

public record PaymentVerificationResult(
    boolean success,
    StoreType storeType,
    String errorMessage
) {

    public static PaymentVerificationResult success(StoreType storeType) {
        return new PaymentVerificationResult(true, storeType, null);
    }

    public static PaymentVerificationResult failure(StoreType storeType, String errorMessage) {
        return new PaymentVerificationResult(false, storeType, errorMessage);
    }
}
