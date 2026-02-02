package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.Payment;
import java.util.Optional;

public interface PaymentStoragePort {

    Payment save(Payment payment);

    Payment findById(Long id);

    Optional<Payment> findBySubscriptionKey(String subscriptionKey);

    Optional<Payment> findByOrderTxId(String orderTxId);
}
