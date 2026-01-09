package com.odcloud.application.voucher.port.in;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.voucher.service.create_voucher.CreateVoucherServiceResponse;

public interface CreateVoucherUseCase {

    CreateVoucherServiceResponse create(CreateVoucherCommand command);
}
