package com.odcloud.application.voucher.port.in;

import com.odcloud.application.voucher.service.create_voucher.CreateVoucherCommand;
import com.odcloud.application.voucher.service.create_voucher.CreateVoucherResponse;

public interface CreateVoucherUseCase {

    CreateVoucherResponse create(CreateVoucherCommand command);
}
