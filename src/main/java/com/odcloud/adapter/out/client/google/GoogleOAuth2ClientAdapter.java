package com.odcloud.adapter.out.client.google;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_USER_INFO_ERROR;

import com.odcloud.application.port.out.GoogleOAuth2Port;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Component
class GoogleOAuth2ClientAdapter implements GoogleOAuth2Port {

    private final GoogleUserInfoClient userInfoClient;

    GoogleOAuth2ClientAdapter(ProfileConstant constant) {
        this.userInfoClient = HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(
                RestClient.builder()
                    .baseUrl(constant.googleOAuth2().userInfoApi())
                    .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                            .withConnectTimeout(Duration.ofSeconds(1))
                            .withReadTimeout(Duration.ofSeconds(5))))
                    .build()))
            .build()
            .createClient(GoogleUserInfoClient.class);
    }

    @Override
    public GoogleUserInfoResponse getUserInfo(String googleAccessToken) {
        try {
            return userInfoClient.getUserInfo(googleAccessToken);
        } catch (Exception e) {
            log.error("GoogleOAuth2ClientAdapter getUserInfo - {}", e.getMessage());
            throw new CustomBusinessException(Business_GOOGLE_USER_INFO_ERROR);
        }
    }
}
