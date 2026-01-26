package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.voucher.port.out.PaymentVerificationPort;

public class FakePaymentVerificationPort implements PaymentVerificationPort {

    public boolean verifyResult = true;

    @Override
    public boolean verify(CreateVoucherCommand command) {
        return verifyResult;
    }

    public void setVerifyResult(boolean result) {
        this.verifyResult = result;
    }
}
