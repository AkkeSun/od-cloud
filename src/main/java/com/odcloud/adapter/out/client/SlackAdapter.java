package com.odcloud.adapter.out.client;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SLACK_CLIENT_ERROR;

import com.odcloud.application.port.out.SlackPort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.util.JsonUtil;
import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
class SlackAdapter implements SlackPort {

    private final SlackClient client;
    private final String token;

    SlackAdapter(ProfileConstant constant) {
        RestClient restClient = RestClient.builder()
            .baseUrl(constant.slack().host())
            .requestFactory(ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                    .withConnectTimeout(Duration.ofSeconds(1))
                    .withReadTimeout(Duration.ofSeconds(5))))
            .build();

        this.token = constant.slack().token();
        this.client = HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(restClient))
            .build()
            .createClient(SlackClient.class);
        ;
    }

    @Override
    public void sendMessage(SlackRequest request) {
        String response = client.sendMessage(request, token);
        String result = JsonUtil.extractJsonField(response, "ok");
        if (result.equals("false")) {
            throw new CustomBusinessException(Business_SLACK_CLIENT_ERROR);
        }
    }
}
