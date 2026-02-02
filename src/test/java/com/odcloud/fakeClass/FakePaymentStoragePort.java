package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.out.PaymentStoragePort;
import com.odcloud.domain.model.Payment;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakePaymentStoragePort implements PaymentStoragePort {

    public List<Payment> database = new ArrayList<>();
    public Long id = 0L;

    @Override
    public Payment save(Payment payment) {
        Payment savedPayment = Payment.builder()
            .id(payment.getId() == null ? id++ : payment.getId())
            .accountId(payment.getAccountId())
            .storeType(payment.getStoreType())
            .subscriptionKey(payment.getSubscriptionKey())
            .orderTxId(payment.getOrderTxId())
            .status(payment.getStatus())
            .storeProcessDt(payment.getStoreProcessDt())
            .regDt(payment.getRegDt())
            .build();

        database.add(savedPayment);
        log.info("FakePaymentStoragePort saved: id={}, orderTxId={}", savedPayment.getId(),
            payment.getOrderTxId());
        return savedPayment;
    }

    @Override
    public Payment findById(Long id) {
        return database.stream()
            .filter(payment -> payment.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PAYMENT));
    }

    @Override
    public Optional<Payment> findBySubscriptionKey(String subscriptionKey) {
        return database.stream()
            .filter(payment -> subscriptionKey.equals(payment.getSubscriptionKey()))
            .reduce(
                (first, second) -> second); // Get the latest one (simulating ORDER BY regDt DESC)
    }

    @Override
    public Optional<Payment> findByOrderTxId(String orderTxId) {
        return database.stream()
            .filter(payment -> orderTxId.equals(payment.getOrderTxId()))
            .findFirst();
    }
}
