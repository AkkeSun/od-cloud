package com.odcloud.adapter.out.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

interface SlackClient {

    @PostExchange("/api/chat.postMessage")
    String sendMessage(
        @RequestBody SlackRequest request,
        @RequestHeader("Authorization") String authorization
    );
}
