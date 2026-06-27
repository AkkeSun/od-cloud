package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Voucher;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "voucher")
class VoucherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "voucher_type")
    private String voucherType;

    @Column(name = "status")
    private String status;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "memo")
    private String memo;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_dt")
    private LocalDateTime endDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static VoucherEntity of(Voucher voucher) {
        return VoucherEntity.builder()
            .id(voucher.getId())
            .paymentId(voucher.getPaymentId())
            .voucherType(voucher.getVoucherType())
            .status(voucher.getStatus())
            .groupId(voucher.getGroupId())
            .memo(voucher.getMemo())
            .startAt(voucher.getStartAt())
            .endDt(voucher.getEndDt())
            .modDt(voucher.getModDt())
            .regDt(voucher.getRegDt())
            .build();
    }

    Voucher toDomain() {
        return Voucher.builder()
            .id(id)
            .paymentId(paymentId)
            .voucherType(voucherType)
            .status(status)
            .groupId(groupId)
            .memo(memo)
            .startAt(startAt)
            .endDt(endDt)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
