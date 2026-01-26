package com.odcloud.application.voucher.port.out;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;

public interface PaymentVerificationPort {

    boolean verify(CreateVoucherCommand command);
}
