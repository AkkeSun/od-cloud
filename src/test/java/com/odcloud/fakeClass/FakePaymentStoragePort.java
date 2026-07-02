package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.domain.model.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakePaymentStoragePort implements PaymentStoragePort {

    public List<Payment> database = new ArrayList<>();

    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Payment save(Payment payment) {
        Payment saved = Payment.builder()
            .id(payment.getId() == null ? sequence.incrementAndGet() : payment.getId())
            .subscriptionId(payment.getSubscriptionId())
            .productId(payment.getProductId())
            .groupId(payment.getGroupId())
            .buyerId(payment.getBuyerId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .billingKey(payment.getBillingKey())
            .modDt(payment.getModDt())
            .regDt(payment.getRegDt())
            .build();
        database.add(saved);
        return saved;
    }
}
