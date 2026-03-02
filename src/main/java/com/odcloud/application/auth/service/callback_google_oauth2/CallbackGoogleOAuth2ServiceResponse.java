package com.odcloud.application.auth.service.callback_google_oauth2;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record CallbackGoogleOAuth2ServiceResponse(
    String googleAccessToken,
    String redirectUri
) {

    public static CallbackGoogleOAuth2ServiceResponse of(String googleAccessToken, String allowedOrigin) {
        String redirectUri = allowedOrigin
            + "/auth/callback?googleAccessToken="
            + URLEncoder.encode(googleAccessToken, StandardCharsets.UTF_8);
        return new CallbackGoogleOAuth2ServiceResponse(googleAccessToken, redirectUri);
    }
}
