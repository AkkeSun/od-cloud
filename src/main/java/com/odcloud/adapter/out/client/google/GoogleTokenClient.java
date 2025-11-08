package com.odcloud.adapter.out.client.google;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

interface GoogleTokenClient {

    @PostExchange("/token")
    GoogleTokenResponse getToken(
        @RequestParam("code") String code,
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam("grant_type") String grantType
    );
}
