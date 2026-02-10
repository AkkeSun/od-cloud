package com.odcloud.application.webhook.service.googleplay;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.voucher.port.out.PaymentStoragePort;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.application.webhook.port.in.HandleGooglePlayWebhookUseCase;
import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Voucher;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class HandleGooglePlayWebhookService implements HandleGooglePlayWebhookUseCase {

    private final PaymentStoragePort paymentStoragePort;
    private final VoucherStoragePort voucherStoragePort;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public void handle(GooglePlayNotificationCommand command) {
        if (command.testNotification()) {
            log.info("Google Play test notification received");
            return;
        }

        if (command.notificationType().isRenewal()) {
            handleRenewal(command);
            return;
        }

        if (command.notificationType().isRefund()) {
            handleRefund(command);
        }
    }

    private void handleRenewal(GooglePlayNotificationCommand command) {
        String purchaseToken = command.purchaseToken();
        log.info("Processing subscription renewal: purchaseToken={}", purchaseToken);

        Optional<Payment> existingPayment = paymentStoragePort.findBySubscriptionKey(purchaseToken);
        if (existingPayment.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PAYMENT);
        }

        Payment payment = existingPayment.get();
        Payment savedPayment = paymentStoragePort.save(payment.createRenewal(command));

        Voucher voucher = voucherStoragePort.findByPaymentId(payment.getId());
        voucher.updateSubscription(savedPayment.getId());
        voucherStoragePort.update(voucher);

        log.info("Subscription renewed: voucherId={}, newPaymentId={}, newEndDt={}",
            voucher.getId(), savedPayment.getId(), voucher.getEndDt());
    }

    private void handleRefund(GooglePlayNotificationCommand command) {
        String purchaseToken = command.purchaseToken();
        log.info("Processing refund: purchaseToken={}, orderId={}", purchaseToken, command.orderId());

        Optional<Payment> existingPayment = paymentStoragePort.findBySubscriptionKey(purchaseToken);
        if (existingPayment.isEmpty() && command.orderId() != null) {
            existingPayment = paymentStoragePort.findByOrderTxId(command.orderId());
        }

        if (existingPayment.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PAYMENT);
        }

        Payment payment = existingPayment.get();
        paymentStoragePort.save(payment.createRefund(command.storeProcessDt()));

        Voucher voucher = voucherStoragePort.findByPaymentId(payment.getId());
        voucher.revoke();
        voucherStoragePort.update(voucher);

        if (voucher.getVoucherType().isStorageVoucher()) {
            for (Group group : groupStoragePort.findByOwnerId(voucher.getAccountId())) {
                redisStoragePort.executeWithLock(GROUP_LOCK + group.getId(), () -> {
                    Group lockedGroup = groupStoragePort.findById(group.getId());
                    lockedGroup.decreaseStorageTotal(
                        voucher.getVoucherType().getStorageIncreaseSize());
                    groupStoragePort.updateStorageTotal(lockedGroup);
                    return null;
                });
            }
        }

        log.info("Voucher revoked: voucherId={}", voucher.getId());
    }
}
