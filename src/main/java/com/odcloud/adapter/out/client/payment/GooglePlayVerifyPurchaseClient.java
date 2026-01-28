package com.odcloud.adapter.out.client.payment;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

interface GooglePlayVerifyPurchaseClient {

    @GetExchange("/{packageName}/purchases/products/{productId}/tokens/{token}")
    GooglePlayVerifyPurchaseResponse verifyPurchase(
        @PathVariable("packageName") String packageName,
        @PathVariable("productId") String productId,
        @PathVariable("token") String token
    );
}
