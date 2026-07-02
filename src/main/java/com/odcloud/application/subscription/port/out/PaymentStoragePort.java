package com.odcloud.application.subscription.port.out;

import com.odcloud.domain.model.Payment;

public interface PaymentStoragePort {

    Payment save(Payment payment);
}
