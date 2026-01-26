package com.odcloud.adapter.out.client.apple;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface AppleVerifyReceiptClient {

    @PostExchange("/verifyReceipt")
    AppleVerifyReceiptResponse verifyReceipt(@RequestBody AppleVerifyReceiptRequest request);
}
