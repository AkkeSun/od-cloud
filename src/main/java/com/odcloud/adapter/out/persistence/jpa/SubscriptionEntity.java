package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Subscription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "subscription")
class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "status")
    private String status;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static SubscriptionEntity of(Subscription subscription) {
        return SubscriptionEntity.builder()
            .id(subscription.getId())
            .productId(subscription.getProductId())
            .groupId(subscription.getGroupId())
            .buyerId(subscription.getBuyerId())
            .status(subscription.getStatus())
            .billingKey(subscription.getBillingKey())
            .nextBillingDate(subscription.getNextBillingDate())
            .expiredDate(subscription.getExpiredDate())
            .modDt(subscription.getModDt())
            .regDt(subscription.getRegDt())
            .build();
    }

    Subscription toDomain() {
        return Subscription.builder()
            .id(id)
            .productId(productId)
            .groupId(groupId)
            .buyerId(buyerId)
            .status(status)
            .billingKey(billingKey)
            .nextBillingDate(nextBillingDate)
            .expiredDate(expiredDate)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
