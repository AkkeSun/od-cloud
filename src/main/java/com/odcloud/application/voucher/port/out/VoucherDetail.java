package com.odcloud.application.voucher.port.out;

import java.time.LocalDateTime;

public record VoucherDetail(
    String groupName,
    String voucherType,
    String payerNickname,
    LocalDateTime endDt
) {

}
