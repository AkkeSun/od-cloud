package com.odcloud.adapter.out.client.google;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

public interface GoogleUserInfoClient {

    @PostExchange("/oauth2/v3/userinfo")
    GoogleUserInfoResponse getUserInfo(
        @RequestHeader(name = "Authorization") String authorization);
}
