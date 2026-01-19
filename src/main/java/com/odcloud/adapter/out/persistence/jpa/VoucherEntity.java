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
@Table(name = "VOUCHER")
class VoucherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "VOUCHER_TYPE")
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private VoucherStatus status;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "START_AT")
    private LocalDateTime startAt;

    @Column(name = "END_DT")
    private LocalDateTime endDt;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;
}
