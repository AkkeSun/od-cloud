package com.odcloud.adapter.out.client.payment;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

interface AppleVerifyReceiptClient {

    @PostExchange("/verifyReceipt")
    AppleVerifyReceiptResponse verifyReceipt(@RequestBody AppleVerifyReceiptRequest request);
}
