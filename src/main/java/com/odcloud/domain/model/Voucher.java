package com.odcloud.domain.model;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    private Long id;
    private Long paymentId;
    private VoucherType voucherType;
    private VoucherStatus status;
    private Long accountId;
    private Long groupId;
    private String memo;
    private LocalDateTime startAt;
    private LocalDateTime endDt;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Voucher create(Long paymentId, CreateVoucherCommand command) {
        LocalDateTime startAt = LocalDateTime.now();
        return Voucher.builder()
            .paymentId(paymentId)
            .voucherType(command.voucherType())
            .status(VoucherStatus.ACTIVE)
            .accountId(command.accountId())
            .groupId(command.groupId())
            .memo(command.memo())
            .startAt(startAt)
            .endDt(command.voucherType().calculateEndDt(startAt))
            .regDt(LocalDateTime.now())
            .build();
    }

    public void expire() {
        this.status = VoucherStatus.EXPIRED;
        this.modDt = LocalDateTime.now();
    }

    public void revoke() {
        this.status = VoucherStatus.REVOKED;
        this.modDt = LocalDateTime.now();
    }

    public void updateSubscription(Long newPaymentId) {
        this.paymentId = newPaymentId;
        this.modDt = LocalDateTime.now();

        if (this.endDt.isBefore(LocalDateTime.now())) {
            this.status = VoucherStatus.ACTIVE;
            this.endDt = LocalDateTime.now().plusDays(this.voucherType.getDurationDays());
        } else {
            this.endDt = this.endDt.plusDays(this.voucherType.getDurationDays());
        }
    }
}


