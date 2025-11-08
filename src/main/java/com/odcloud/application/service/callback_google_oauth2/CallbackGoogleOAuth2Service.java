package com.odcloud.application.service.callback_google_oauth2;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import com.odcloud.application.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.port.out.GoogleOAuth2Port;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CallbackGoogleOAuth2Service implements CallbackGoogleOAuth2UseCase {

    private final GoogleOAuth2Port googleOAuth2Port;

    @Override
    public CallbackGoogleOAuth2ServiceResponse callback(String code) {
        GoogleTokenResponse tokenResponse = googleOAuth2Port.getToken(code);
        return CallbackGoogleOAuth2ServiceResponse.of(tokenResponse);
    }
}
