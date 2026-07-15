package com.odcloud.application.subscription.service.renew_subscriptions;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.subscription.port.in.RenewSubscriptionsUseCase;
import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.application.subscription.port.out.PgClientPort;
import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class RenewSubscriptionsService implements RenewSubscriptionsUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;
    private final PaymentStoragePort paymentStoragePort;
    private final ProductStoragePort productStoragePort;
    private final PgClientPort pgClientPort;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public RenewSubscriptionsResponse renew() {
        List<Subscription> targets =
            subscriptionStoragePort.findByRenewTargets(LocalDate.now());

        int successCount = 0;
        int failCount = 0;

        for (Subscription subscription : targets) {
            try {
                Product product = productStoragePort.findById(subscription.getProductId());
                if (!pgClientPort.pay(subscription.getBillingKey(), product.getPrice())) {
                    log.warn("[RenewSubscriptionsService] 결제 실패 - subscriptionId={}",
                        subscription.getId());
                    failCount++;
                    continue;
                }

                LocalDateTime now = LocalDateTime.now();
                paymentStoragePort.save(Payment.builder()
                    .subscriptionId(subscription.getId())
                    .buyerId(subscription.getBuyerId())
                    .amount(product.getPrice())
                    .status("PAID")
                    .regDt(now)
                    .build());

                boolean pendingActivation = subscription.isPending();
                subscription.renew();
                if (pendingActivation) {
                    subscription.activate();
                }
                subscriptionStoragePort.save(subscription);

                if (pendingActivation) {
                    applyGroupBenefit(subscription);
                }
                successCount++;
            } catch (Exception e) {
                log.error("[RenewSubscriptionsService] 갱신 처리 실패 - subscriptionId={}, error={}",
                    subscription.getId(), e.getMessage());
                failCount++;
            }
        }

        return RenewSubscriptionsResponse.builder()
            .totalCount(targets.size())
            .successCount(successCount)
            .failCount(failCount)
            .build();
    }

    private void applyGroupBenefit(Subscription subscription) {
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
