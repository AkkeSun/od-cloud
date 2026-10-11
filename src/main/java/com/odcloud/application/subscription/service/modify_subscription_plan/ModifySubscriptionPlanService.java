package com.odcloud.application.subscription.service.modify_subscription_plan;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;
import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_PLAN_CHANGE;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_MODIFY;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_PLAN_CHANGE_PAYMENT_FAILED;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.subscription.port.in.ModifySubscriptionPlanUseCase;
import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.application.subscription.port.out.PgClientPort;
import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.domain.service.SubscriptionPlanChangeCalculator;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.math.BigDecimal;
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
class ModifySubscriptionPlanService implements ModifySubscriptionPlanUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;
    private final ProductStoragePort productStoragePort;
    private final PaymentStoragePort paymentStoragePort;
    private final PgClientPort pgClientPort;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    private final SubscriptionPlanChangeCalculator calculator = new SubscriptionPlanChangeCalculator();

    @Override
    @Transactional
    public ModifySubscriptionPlanResponse modify(ModifySubscriptionPlanCommand command) {
        Subscription currentSubscription =
            subscriptionStoragePort.findByIdForUpdate(command.currentSubscriptionId());

        if (!currentSubscription.getBuyerId().equals(command.account().getId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        if (!currentSubscription.isActive()) {
            throw new CustomBusinessException(Business_INVALID_SUBSCRIPTION_STATUS_FOR_MODIFY);
        }

        Product currentProduct = productStoragePort.findById(currentSubscription.getProductId());
        Product newProduct = productStoragePort.findById(command.newProductId());

        boolean isUpgrade = calculator.isUpgrade(currentProduct.getPrice(), newProduct.getPrice());
        boolean isDowngrade = calculator.isDowngrade(currentProduct.getPrice(),
            newProduct.getPrice());

        if (!isUpgrade && !isDowngrade) {
            throw new CustomBusinessException(Business_INVALID_PLAN_CHANGE);
        }

        if (subscriptionStoragePort.existsActiveByGroupIdAndProductId(
            currentSubscription.getGroupId(), command.newProductId())
        ) {
            throw new CustomBusinessException(Business_ALREADY_EXISTS_SUBSCRIPTION);
        }

        return isUpgrade ?
            upgrade(command, currentSubscription, currentProduct, newProduct) :
            downgrade(command, currentSubscription);
    }

    private ModifySubscriptionPlanResponse upgrade(
        ModifySubscriptionPlanCommand command,
        Subscription currentSubscription,
        Product currentProduct,
        Product newProduct
    ) {
        LocalDate today = LocalDate.now();
        BigDecimal remainingValue =
            calculator.calculateRemainingValue(currentProduct.getPrice(), currentSubscription,
                today);
        BigDecimal chargeAmount =
            calculator.calculateUpgradeChargeAmount(newProduct.getPrice(), remainingValue);

        if (!pgClientPort.pay(currentSubscription.getBillingKey(), chargeAmount)) {
            log.warn("[ModifySubscriptionPlanService] 플랜 변경 차액 결제 실패 - subscriptionId={}",
                currentSubscription.getId());
            throw new CustomBusinessException(Business_PLAN_CHANGE_PAYMENT_FAILED);
        }

        currentSubscription.terminateImmediately();
        subscriptionStoragePort.save(currentSubscription);

        // 신규 구독을 저장하기 전에 그룹 혜택을 원복+재적용한다. 이 시점에는 아직 신규 구독이
        // 존재하지 않으므로 remainingActive 조회 결과에 신규 구독이 섞여 들어갈 여지가 없다.
        // (신규 구독을 먼저 저장한 뒤 원복을 계산하면, applyBenefit 이 덮어써서 우연히 결과가
        // 맞아떨어지는 것처럼 보일 뿐 로직 순서에 의존하는 취약한 구조가 된다.)
        applyPlanChangeToGroup(currentSubscription, command.newProductId());

        LocalDate nextBillingDate = today.plusMonths(1);
        Subscription newSubscription = subscriptionStoragePort.save(Subscription.builder()
            .productId(command.newProductId())
            .groupId(currentSubscription.getGroupId())
            .buyerId(currentSubscription.getBuyerId())
            .status("ACTIVE")
            .billingKey(currentSubscription.getBillingKey())
            .nextBillingDate(nextBillingDate)
            .expiredDate(nextBillingDate)
            .regDt(LocalDateTime.now())
            .build());

        Payment savedPayment = paymentStoragePort.save(Payment.builder()
            .subscriptionId(newSubscription.getId())
            .buyerId(currentSubscription.getBuyerId())
            .amount(chargeAmount)
            .status("PAID")
            .regDt(LocalDateTime.now())
            .build());

        return ModifySubscriptionPlanResponse.ofUpgrade(
            currentSubscription.getId(),
            newSubscription.getId(),
            savedPayment.getId(),
            chargeAmount
        );
    }

    private ModifySubscriptionPlanResponse downgrade(
        ModifySubscriptionPlanCommand command,
        Subscription currentSubscription
    ) {
        currentSubscription.downgradePending();
        subscriptionStoragePort.save(currentSubscription);

        Subscription newSubscription = subscriptionStoragePort.save(Subscription.builder()
            .productId(command.newProductId())
            .groupId(currentSubscription.getGroupId())
            .buyerId(currentSubscription.getBuyerId())
            .status("PENDING")
            .billingKey(currentSubscription.getBillingKey())
            .nextBillingDate(currentSubscription.getExpiredDate())
            .expiredDate(currentSubscription.getExpiredDate())
            .regDt(LocalDateTime.now())
            .build());

        return ModifySubscriptionPlanResponse.ofDowngrade(
            currentSubscription.getId(),
            newSubscription.getId()
        );
    }

    private void applyPlanChangeToGroup(Subscription currentSubscription, Long newProductId) {
        redisStoragePort.executeWithLock(GROUP_LOCK + currentSubscription.getGroupId(), () -> {
            Group group = groupStoragePort.findById(currentSubscription.getGroupId());
            List<SubscriptionDetail> remainingActive = subscriptionStoragePort.findActiveByGroupIds(
                List.of(currentSubscription.getGroupId()));

            List<Long> activeProductIds = new ArrayList<>(remainingActive.stream()
                .filter(detail -> detail.subscriptionId() != null)
                .map(SubscriptionDetail::productId)
                .toList());
            activeProductIds.add(newProductId);

            group.applyBenefit(activeProductIds);
            groupStoragePort.updateBenefit(group);
            return null;
        });
    }
}
