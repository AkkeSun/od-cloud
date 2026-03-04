package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2Response;

public interface CallbackGoogleOAuth2UseCase {

    CallbackGoogleOAuth2Response callback(String code);
}
