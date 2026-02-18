package com.odcloud.application.voucher.port.in.command;

import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateVoucherCommand(
    Long accountId,
    StoreType storeType,
    String subscriptionKey,
    String orderTxId,
    LocalDateTime storeProcessDt,
    VoucherType voucherType,
    String memo
) {

}
