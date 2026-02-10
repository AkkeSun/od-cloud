package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type")
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VoucherStatus status;

    @Column(name = "account_id")
    private Long accountId;

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
}
