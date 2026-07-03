package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PaymentStorageAdapter implements PaymentStoragePort {

    private final PaymentRepository repository;

    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }
}
