package com.odcloud.application.voucher.service.create_voucher;

public record CreateVoucherResponse(
    Boolean result
) {

    public static CreateVoucherResponse ofSuccess() {
        return new  CreateVoucherResponse(Boolean.TRUE);
    }
}
