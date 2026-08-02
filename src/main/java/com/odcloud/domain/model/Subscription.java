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

    public boolean isCancelable() {
        return "ACTIVE".equals(status) || "DOWN_PENDING".equals(status);
    }

    public boolean isDownPending() {
        return "DOWN_PENDING".equals(status);
    }

    public boolean isReactivatable() {
        return "EXP_PENDING".equals(status);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public void activate() {
        this.status = "ACTIVE";
        this.modDt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = "EXP_PENDING";
        this.modDt = LocalDateTime.now();
    }

    public void reactivate() {
        this.status = "ACTIVE";
        this.modDt = LocalDateTime.now();
    }

    public void renew() {
        // 테스트 용도: 구독 등록(RegisterSubscriptionService)과 동일하게 갱신 주기도 다음날로 설정한다.
        // 갱신 주기를 한 달로 두면 최초 등록 이후 만료일이 한 달 뒤로 밀려, 만료일을 기준으로
        // 반영되는 다운그레이드 예약이 테스트 환경에서 다음날 적용되지 않는다.
        this.nextBillingDate = this.nextBillingDate.plusDays(1);
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

    public void cancelDowngradeReservation() {
        this.status = "ACTIVE";
        this.modDt = LocalDateTime.now();
    }
}
