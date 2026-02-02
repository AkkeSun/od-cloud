package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_PAYMENT;

import com.odcloud.application.voucher.port.out.PaymentStoragePort;
import com.odcloud.domain.model.Payment;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class PaymentStorageAdapter implements PaymentStoragePort {

    private final PaymentRepository repository;

    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_PAYMENT));
    }

    @Override
    public Optional<Payment> findBySubscriptionKey(String subscriptionKey) {
        return repository.findBySubscriptionKey(subscriptionKey);
    }

    @Override
    public Optional<Payment> findByOrderTxId(String orderTxId) {
        return repository.findByOrderTxId(orderTxId);
    }
}
