package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "payment")
class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    static PaymentEntity of(Payment payment) {
        return PaymentEntity.builder()
            .id(payment.getId())
            .subscriptionId(payment.getSubscriptionId())
            .buyerId(payment.getBuyerId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .modDt(payment.getModDt())
            .regDt(payment.getRegDt())
            .build();
    }

    Payment toDomain() {
        return Payment.builder()
            .id(id)
            .subscriptionId(subscriptionId)
            .buyerId(buyerId)
            .amount(amount)
            .status(status)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
