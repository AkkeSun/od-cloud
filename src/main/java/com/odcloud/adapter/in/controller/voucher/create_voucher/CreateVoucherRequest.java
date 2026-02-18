package com.odcloud.adapter.in.controller.voucher.create_voucher;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.infrastructure.util.DateUtil;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateVoucherRequest(
    @NotNull(message = "스토어 타입은 필수값 입니다")
    StoreType storeType,

    @NotBlank(message = "구독 고유키는 필수값 입니다")
    String subscriptionKey,

    @NotBlank(message = "결제 단위 트랜젝션 아이디는 필수값 입니다")
    String orderTxId,

    @NotBlank(message = "스토어 처리 일시는 필수값 입니다")
    String storeProcessDt,

    @NotNull(message = "바우처 타입은 필수값 입니다")
    VoucherType voucherType,

    String memo
) {

    CreateVoucherCommand toCommand(Account account) {
        return CreateVoucherCommand.builder()
            .accountId(account.getId())
            .storeType(storeType)
            .subscriptionKey(subscriptionKey)
            .orderTxId(orderTxId)
            .storeProcessDt(DateUtil.parse(storeProcessDt))
            .voucherType(voucherType)
            .memo(memo)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
