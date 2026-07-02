package com.odcloud.application.subscription.service.register_subscription;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_BILLING_KEY;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.subscription.port.in.RegisterSubscriptionUseCase;
import com.odcloud.application.subscription.port.out.PaymentStoragePort;
import com.odcloud.application.subscription.port.out.PgClientPort;
import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public RegisterSubscriptionResponse register(RegisterSubscriptionCommand command) {
        // userId 유효성 검증 (등록된 사용자인지)
        Account buyer = accountStoragePort.findByEmail(command.account().getEmail());

        // groupId 유효성 검증 (가입한 그룹인지 + 등록된 그룹인지)
        if (command.groupAccessDenied()) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        // productId 유효성 검증 (등록된 상품인지)
        Product product = productStoragePort.findById(command.productId());

        // 이미 활성화된 동일 groupId + productId 구독이 있는지 확인
        if (subscriptionStoragePort.existsActiveByGroupIdAndProductId(command.groupId(), command.productId())) {
            throw new CustomBusinessException(Business_ALREADY_EXISTS_SUBSCRIPTION);
        }

        // billingKey 유효성 검증 (PG 사)
        if (!pgClientPort.verifyBillingKey(command.billingKey())) {
            throw new CustomBusinessException(Business_INVALID_BILLING_KEY);
        }

        LocalDateTime now = LocalDateTime.now();

        Subscription savedSubscription = subscriptionStoragePort.save(Subscription.builder()
            .productId(command.productId())
            .groupId(command.groupId())
            .buyerId(buyer.getId())
            .status("ACTIVE")
            .billingKey(command.billingKey())
            .nextBillingDate(LocalDate.now().plusMonths(1))
            .regDt(now)
            .build());

        Payment savedPayment = paymentStoragePort.save(Payment.builder()
            .subscriptionId(savedSubscription.getId())
            .productId(command.productId())
            .groupId(command.groupId())
            .buyerId(buyer.getId())
            .amount(product.getPrice())
            .status("PAID")
            .billingKey(command.billingKey())
            .regDt(now)
            .build());

        return RegisterSubscriptionResponse.of(savedSubscription.getId(), savedPayment.getId());
    }
}
