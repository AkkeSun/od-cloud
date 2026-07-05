package com.odcloud.adapter.in.scheduler;

import com.odcloud.application.subscription.port.in.ExpireSubscriptionsUseCase;
import com.odcloud.application.subscription.port.in.RenewSubscriptionsUseCase;
import com.odcloud.application.subscription.service.expire_subscriptions.ExpireSubscriptionsResponse;
import com.odcloud.application.subscription.service.renew_subscriptions.RenewSubscriptionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SubscriptionScheduler {

    private final RenewSubscriptionsUseCase renewSubscriptionsUseCase;
    private final ExpireSubscriptionsUseCase expireSubscriptionsUseCase;

    @Scheduled(cron = "0 0 1 * * *")
    @SchedulerLock(
        name = "SubscriptionScheduler_renewSubscriptions",
        lockAtLeastFor = "1m",
        lockAtMostFor = "30m"
    )
    public void renewSubscriptions() {
        log.info("[SubscriptionScheduler] 구독 갱신 결제 시작");
        RenewSubscriptionsResponse result = renewSubscriptionsUseCase.renew();
        log.info("[SubscriptionScheduler] 구독 갱신 완료 - total={}, success={}, fail={}",
            result.totalCount(), result.successCount(), result.failCount());
    }

    @Scheduled(cron = "0 30 1 * * *")
    @SchedulerLock(
        name = "SubscriptionScheduler_expireSubscriptions",
        lockAtLeastFor = "1m",
        lockAtMostFor = "30m"
    )
    public void expireSubscriptions() {
        log.info("[SubscriptionScheduler] 구독 만료 처리 시작");
        ExpireSubscriptionsResponse result = expireSubscriptionsUseCase.expire();
        log.info("[SubscriptionScheduler] 구독 만료 완료 - total={}, success={}, fail={}",
            result.totalCount(), result.successCount(), result.failCount());
    }
}
