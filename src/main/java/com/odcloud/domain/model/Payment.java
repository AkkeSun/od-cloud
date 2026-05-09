package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long accountId;
    private StoreType storeType;
    private String subscriptionKey;
    private String orderTxId;
    private PaymentStatus status;
    private LocalDateTime storeProcessDt;
    private LocalDateTime regDt;

    public Payment createRenewal(String subscriptionId, LocalDateTime storeProcessDt) {
        return Payment.builder()
            .accountId(accountId)
            .storeType(storeType)
            .subscriptionKey(subscriptionKey)
            .orderTxId(subscriptionId)
            .status(PaymentStatus.PAID)
            .storeProcessDt(storeProcessDt)
            .regDt(LocalDateTime.now())
            .build();
    }

    public Payment createRefund(LocalDateTime refundDt) {
        return Payment.builder()
            .accountId(accountId)
            .storeType(storeType)
            .subscriptionKey(subscriptionKey)
            .orderTxId(orderTxId)
            .status(PaymentStatus.REFUNDED)
            .storeProcessDt(refundDt)
            .regDt(LocalDateTime.now())
            .build();
    }
}
