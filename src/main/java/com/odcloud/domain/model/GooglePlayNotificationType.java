package com.odcloud.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GooglePlayNotificationType {
    // Subscription notifications
    SUBSCRIPTION_RECOVERED(1, "구독이 일시중지에서 복구됨"),
    SUBSCRIPTION_RENEWED(2, "활성 구독이 갱신됨"),
    SUBSCRIPTION_CANCELED(3, "구독이 자발적 또는 비자발적으로 취소됨"),
    SUBSCRIPTION_PURCHASED(4, "새 구독이 구매됨"),
    SUBSCRIPTION_ON_HOLD(5, "구독이 계정 보류 상태로 전환됨"),
    SUBSCRIPTION_IN_GRACE_PERIOD(6, "구독이 유예 기간에 진입함"),
    SUBSCRIPTION_RESTARTED(7, "사용자가 Play > 계정 > 구독에서 구독을 복원함"),
    SUBSCRIPTION_PRICE_CHANGE_CONFIRMED(8, "사용자가 가격 변경을 확인함"),
    SUBSCRIPTION_DEFERRED(9, "구독 갱신 시점이 연장됨"),
    SUBSCRIPTION_PAUSED(10, "구독이 일시중지됨"),
    SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED(11, "구독 일시중지 일정이 변경됨"),
    SUBSCRIPTION_REVOKED(12, "구독이 만료 시간 전에 사용자에 의해 해지됨"),
    SUBSCRIPTION_EXPIRED(13, "구독이 만료됨"),
    SUBSCRIPTION_PENDING_PURCHASE_CANCELED(20, "구독 대기중 구매가 취소됨"),

    // One-time product notifications
    ONE_TIME_PRODUCT_PURCHASED(1, "일회성 제품이 구매됨"),
    ONE_TIME_PRODUCT_CANCELED(2, "일회성 제품 대기중 구매가 취소됨"),

    UNKNOWN(-1, "알 수 없는 알림 타입");

    private final int code;
    private final String description;

    public static GooglePlayNotificationType fromSubscriptionCode(int code) {
        for (GooglePlayNotificationType type : values()) {
            if (type.code == code && type.name().startsWith("SUBSCRIPTION_")) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public static GooglePlayNotificationType fromOneTimeCode(int code) {
        for (GooglePlayNotificationType type : values()) {
            if (type.code == code && type.name().startsWith("ONE_TIME_")) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isRenewal() {
        return this == SUBSCRIPTION_RENEWED || this == SUBSCRIPTION_RECOVERED || this == SUBSCRIPTION_RESTARTED;
    }

    public boolean isRefund() {
        return this == SUBSCRIPTION_REVOKED;
    }
}
