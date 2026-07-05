package com.odcloud.application.subscription.service.expire_subscriptions;

import com.odcloud.application.subscription.port.in.ExpireSubscriptionsUseCase;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class ExpireSubscriptionsService implements ExpireSubscriptionsUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;

    @Override
    @Transactional
    public ExpireSubscriptionsResponse expire() {
        List<Subscription> targets =
            subscriptionStoragePort.findByStatusAndExpiredDateLoe("EXP_PENDING", LocalDateTime.now());

        int successCount = 0;
        int failCount = 0;

        for (Subscription subscription : targets) {
            try {
                subscription.expire();
                subscriptionStoragePort.save(subscription);
                successCount++;
            } catch (Exception e) {
                log.error("[ExpireSubscriptionsService] 만료 처리 실패 - subscriptionId={}, error={}",
                    subscription.getId(), e.getMessage());
                failCount++;
            }
        }

        return ExpireSubscriptionsResponse.builder()
            .totalCount(targets.size())
            .successCount(successCount)
            .failCount(failCount)
            .build();
    }
}
