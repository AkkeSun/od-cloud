package com.odcloud.application.subscription.service.register_subscription;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;
import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_BILLING_KEY;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.subscription.port.in.RegisterSubscriptionUseCase;
import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.application.subscription.port.out.PgClientPort;
import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterSubscriptionService implements RegisterSubscriptionUseCase {

    private final AccountStoragePort accountStoragePort;
    private final SubscriptionStoragePort subscriptionStoragePort;
    private final PaymentStoragePort paymentStoragePort;
    private final ProductStoragePort productStoragePort;
    private final PgClientPort pgClientPort;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public RegisterSubscriptionResponse register(RegisterSubscriptionCommand command) {
        Account buyer = accountStoragePort.findByEmail(command.account().getEmail());
        if (command.groupAccessDenied()) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        Product product = productStoragePort.findById(command.productId());
        if (subscriptionStoragePort.existsActiveByGroupIdAndProductId(command.groupId(),
            command.productId())) {
            throw new CustomBusinessException(Business_ALREADY_EXISTS_SUBSCRIPTION);
        }

        if (!pgClientPort.verifyBillingKey(command.billingKey())) {
            throw new CustomBusinessException(Business_INVALID_BILLING_KEY);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate nextBillingDate = LocalDate.now().plusDays(1);

        Subscription savedSubscription = subscriptionStoragePort.save(Subscription.builder()
            .productId(command.productId())
            .groupId(command.groupId())
            .buyerId(buyer.getId())
            .status("ACTIVE")
            .billingKey(command.billingKey())
            .nextBillingDate(nextBillingDate)
            .expiredDate(nextBillingDate)
            .regDt(now)
            .build());

        Payment savedPayment = paymentStoragePort.save(Payment.builder()
            .subscriptionId(savedSubscription.getId())
            .buyerId(buyer.getId())
            .amount(product.getPrice())
            .status("PAID")
            .regDt(now)
            .build());

        redisStoragePort.executeWithLock(GROUP_LOCK + command.groupId(), () -> {
            Group group = groupStoragePort.findById(command.groupId());
            List<Long> activeProductIds = activeProductIdsIncluding(command.groupId(),
                command.productId());
            group.applyBenefit(activeProductIds);
            groupStoragePort.updateBenefit(group);
            return null;
        });

        return RegisterSubscriptionResponse.of(savedSubscription.getId(), savedPayment.getId());
    }

    private List<Long> activeProductIdsIncluding(Long groupId, Long productId) {
        List<Long> activeProductIds = new ArrayList<>(
            subscriptionStoragePort.findActiveByGroupIds(List.of(groupId)).stream()
                .filter(detail -> detail.subscriptionId() != null)
                .map(SubscriptionDetail::productId)
                .toList());
        activeProductIds.add(productId);
        return activeProductIds;
    }
}
