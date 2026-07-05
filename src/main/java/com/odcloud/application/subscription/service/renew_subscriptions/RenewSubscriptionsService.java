package com.odcloud.application.subscription.service.renew_subscriptions;

import com.odcloud.application.subscription.port.in.RenewSubscriptionsUseCase;
import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.application.subscription.port.out.PgClientPort;
import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
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

    @Override
    @Transactional
    public RenewSubscriptionsResponse renew() {
        List<Subscription> targets =
            subscriptionStoragePort.findByStatusAndNextBillingDateLoe("ACTIVE", LocalDate.now());

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

                subscription.renew();
                subscriptionStoragePort.save(subscription);
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
}
