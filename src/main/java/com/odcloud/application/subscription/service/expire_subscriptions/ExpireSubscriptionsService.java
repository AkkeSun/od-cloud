package com.odcloud.application.subscription.service.expire_subscriptions;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.subscription.port.in.ExpireSubscriptionsUseCase;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Subscription;
import java.time.LocalDate;
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
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public ExpireSubscriptionsResponse expire() {
        List<Subscription> targets =
            subscriptionStoragePort.findExpiredTargets(LocalDate.now());

        int successCount = 0;
        int failCount = 0;

        for (Subscription subscription : targets) {
            try {
                subscription.expire();
                subscriptionStoragePort.save(subscription);
                revertGroupBenefit(subscription);
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

    private void revertGroupBenefit(Subscription subscription) {
        redisStoragePort.executeWithLock(GROUP_LOCK + subscription.getGroupId(), () -> {
            Group group = groupStoragePort.findById(subscription.getGroupId());
            List<SubscriptionDetail> remainingActive =
                subscriptionStoragePort.findActiveByGroupIds(List.of(subscription.getGroupId()));

            List<Long> activeProductIds = remainingActive.stream()
                .filter(detail -> detail.subscriptionId() != null)
                .map(SubscriptionDetail::productId)
                .toList();

            group.applyBenefit(activeProductIds);
            groupStoragePort.updateBenefit(group);
            return null;
        });
    }
}
