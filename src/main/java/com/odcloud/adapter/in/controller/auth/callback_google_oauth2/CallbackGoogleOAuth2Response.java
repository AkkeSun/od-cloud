package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import com.odcloud.application.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;

record CallbackGoogleOAuth2Response(
    String googleAccessToken
) {

    static CallbackGoogleOAuth2Response of(CallbackGoogleOAuth2ServiceResponse serviceResponse) {
        return new CallbackGoogleOAuth2Response(serviceResponse.googleAccessToken());
    }
}
