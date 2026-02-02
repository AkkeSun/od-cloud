package com.odcloud.domain.model;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
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

    public static Payment create(CreateVoucherCommand command) {
        return Payment.builder()
            .accountId(command.accountId())
            .storeType(command.storeType())
            .subscriptionKey(command.subscriptionKey())
            .orderTxId(command.orderTxId())
            .status(PaymentStatus.PAID)
            .storeProcessDt(command.storeProcessDt())
            .regDt(LocalDateTime.now())
            .build();
    }

    public Payment createRenewal(
        GooglePlayNotificationCommand command
    ) {
        return Payment.builder()
            .accountId(accountId)
            .storeType(storeType)
            .subscriptionKey(subscriptionKey)
            .orderTxId(command.subscriptionId())
            .status(PaymentStatus.PAID)
            .storeProcessDt(command.storeProcessDt())
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
