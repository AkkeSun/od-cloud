package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.voucher.port.out.PaymentVerificationPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakePaymentVerificationPort implements PaymentVerificationPort {

    public boolean shouldReturnSuccess = true;

    @Override
    public boolean verify(CreateVoucherCommand command) {
        log.info("FakePaymentVerificationPort verify: storeType={}, subscriptionKey={}",
            command.storeType(), command.subscriptionKey());
        return shouldReturnSuccess;
    }

    public void setVerificationResult(boolean result) {
        this.shouldReturnSuccess = result;
    }

    public void reset() {
        this.shouldReturnSuccess = true;
    }
}
