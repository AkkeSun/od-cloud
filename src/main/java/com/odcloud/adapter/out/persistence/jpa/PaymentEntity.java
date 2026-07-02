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

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static PaymentEntity of(Payment payment) {
        return PaymentEntity.builder()
            .id(payment.getId())
            .subscriptionId(payment.getSubscriptionId())
            .productId(payment.getProductId())
            .groupId(payment.getGroupId())
            .buyerId(payment.getBuyerId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .billingKey(payment.getBillingKey())
            .modDt(payment.getModDt())
            .regDt(payment.getRegDt())
            .build();
    }

    Payment toDomain() {
        return Payment.builder()
            .id(id)
            .subscriptionId(subscriptionId)
            .productId(productId)
            .groupId(groupId)
            .buyerId(buyerId)
            .amount(amount)
            .status(status)
            .billingKey(billingKey)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
