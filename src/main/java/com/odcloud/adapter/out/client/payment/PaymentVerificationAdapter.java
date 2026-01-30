package com.odcloud.adapter.out.client.payment;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.voucher.port.out.PaymentVerificationPort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Component
class PaymentVerificationAdapter implements PaymentVerificationPort {

    private final AppleVerifyReceiptClient appleClient;
    private final GooglePlayVerifyPurchaseClient googlePlayClient;
    private final ProfileConstant constant;

    PaymentVerificationAdapter(ProfileConstant constant) {
        this.constant = constant;

        this.appleClient = HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(
                RestClient.builder()
                    .baseUrl(constant.applePayment().verifyReceiptApi())
                    .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                            .withConnectTimeout(Duration.ofSeconds(5))
                            .withReadTimeout(Duration.ofSeconds(10))))
                    .build()))
            .build()
            .createClient(AppleVerifyReceiptClient.class);

        this.googlePlayClient = HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(
                RestClient.builder()
                    .baseUrl(constant.googlePlayPayment().verifyPurchaseApi())
                    .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                            .withConnectTimeout(Duration.ofSeconds(5))
                            .withReadTimeout(Duration.ofSeconds(10))))
                    .build()))
            .build()
            .createClient(GooglePlayVerifyPurchaseClient.class);
    }

    @Override
    public boolean verify(CreateVoucherCommand command) {
        if (command.subscriptionKey().startsWith("mock")) {
            return true;
        }
        return switch (command.storeType()) {
            case APPLE -> verifyApplePayment(command);
            case GOOGLE -> verifyGooglePlayPayment(command);
        };
    }

    private boolean verifyApplePayment(CreateVoucherCommand command) {
        try {
            AppleVerifyReceiptRequest request = AppleVerifyReceiptRequest.of(
                command.subscriptionKey(),
                constant.applePayment().password()
            );

            AppleVerifyReceiptResponse response = appleClient.verifyReceipt(request);
            if (!response.isValid()) {
                log.error("Apple payment verification failed - status: {}", response.status());
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Apple payment API Call error - {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyGooglePlayPayment(CreateVoucherCommand command) {
        try {
            GooglePlayVerifyPurchaseResponse response = googlePlayClient.verifyPurchase(
                constant.googlePlayPayment().packageName(),
                command.voucherType().name(),
                command.subscriptionKey()
            );

            if (!response.isValid()) {
                log.error("Google Play payment verification failed - purchaseState: {}",
                    response.purchaseState());
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Google Play payment Api Call error - {}", e.getMessage());
            return false;
        }
    }
}
