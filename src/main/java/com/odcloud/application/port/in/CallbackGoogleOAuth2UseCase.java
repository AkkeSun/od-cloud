package com.odcloud.application.port.in;

import com.odcloud.application.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;

public interface CallbackGoogleOAuth2UseCase {

    CallbackGoogleOAuth2ServiceResponse callback(String code);
}
