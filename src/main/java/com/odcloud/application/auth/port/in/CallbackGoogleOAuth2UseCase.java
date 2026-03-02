package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;

public interface CallbackGoogleOAuth2UseCase {

    CallbackGoogleOAuth2ServiceResponse callback(String code);
}
