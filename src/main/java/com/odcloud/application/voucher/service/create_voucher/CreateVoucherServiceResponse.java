package com.odcloud.application.voucher.service.create_voucher;

public record CreateVoucherServiceResponse(
    Boolean result
) {

    public static CreateVoucherServiceResponse ofSuccess() {
        return new  CreateVoucherServiceResponse(Boolean.TRUE);
    }
}
