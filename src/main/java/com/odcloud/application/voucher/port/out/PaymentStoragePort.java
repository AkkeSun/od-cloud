package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.Payment;

public interface PaymentStoragePort {

    Payment save(Payment payment);

    Payment findById(Long id);
}
