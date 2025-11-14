package com.odcloud.application.service.callback_google_oauth2;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;

public record CallbackGoogleOAuth2ServiceResponse(
    String googleAccessToken
) {

    public static CallbackGoogleOAuth2ServiceResponse of(GoogleTokenResponse tokenResponse) {
        return new CallbackGoogleOAuth2ServiceResponse("Bearer " + tokenResponse.access_token());
    }
}
