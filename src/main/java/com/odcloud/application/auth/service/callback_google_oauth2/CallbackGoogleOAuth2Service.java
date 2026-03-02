package com.odcloud.application.auth.service.callback_google_oauth2;

import com.odcloud.application.auth.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.infrastructure.constant.ProfileConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CallbackGoogleOAuth2Service implements CallbackGoogleOAuth2UseCase {

    private final GoogleOAuth2Port googleOAuth2Port;
    private final ProfileConstant profileConstant;

    @Override
    public CallbackGoogleOAuth2ServiceResponse callback(String code) {
        String googleAccessToken = "Bearer " + googleOAuth2Port.getToken(code).access_token();
        return CallbackGoogleOAuth2ServiceResponse.of(googleAccessToken,
            profileConstant.googleOAuth2().allowedOrigin());
    }
}
