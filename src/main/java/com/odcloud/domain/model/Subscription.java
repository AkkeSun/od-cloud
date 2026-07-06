package com.odcloud.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    private Long id;
    private Long productId;
    private Long groupId;
    private Long buyerId;
    private String status;
    private String billingKey;
    private LocalDate nextBillingDate;
    private LocalDate expiredDate;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public void cancel() {
        this.status = "EXP_PENDING";
        this.modDt = LocalDateTime.now();
    }

    public void renew() {
        this.nextBillingDate = this.nextBillingDate.plusMonths(1);
        this.expiredDate = this.nextBillingDate;
        this.modDt = LocalDateTime.now();
    }

    public void expire() {
        this.status = "EXPIRED";
        this.modDt = LocalDateTime.now();
    }

    public void terminateImmediately() {
        this.status = "EXPIRED";
        this.expiredDate = LocalDate.now();
        this.modDt = LocalDateTime.now();
    }

    public void downgradePending() {
        this.status = "DOWN_PENDING";
        this.modDt = LocalDateTime.now();
    }
}
