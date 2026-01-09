package com.odcloud.adapter.in.controller.voucher.create_voucher;

import com.odcloud.application.voucher.service.create_voucher.CreateVoucherServiceResponse;

record CreateVoucherResponse(
    Boolean result
) {

    static CreateVoucherResponse of(CreateVoucherServiceResponse serviceResponse) {
        return new CreateVoucherResponse(serviceResponse.result());
    }
}
