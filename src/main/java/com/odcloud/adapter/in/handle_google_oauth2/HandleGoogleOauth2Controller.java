package com.odcloud.adapter.in.handle_google_oauth2;

import com.odcloud.infrastructure.constant.ProfileConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequiredArgsConstructor
class HandleGoogleOauth2Controller {

    private final ProfileConstant constant;

    @GetMapping("/auth/google")
    public ResponseEntity<?> handleGoogleCallback(
        @RequestParam("code") String code) {

        RestClient client = RestClient.create();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", constant.googleOAuth2().clientId());
        formData.add("client_secret", constant.googleOAuth2().clientSecret());
        formData.add("redirect_uri", constant.googleOAuth2().redirectUri());
        formData.add("grant_type", "authorization_code");

        GoogleTokenResponse tokenResponse = client.post()
            .uri("https://oauth2.googleapis.com/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(formData)
            .retrieve()
            .body(GoogleTokenResponse.class);

        return ResponseEntity.ok(tokenResponse);
    }

    @Data
    static class GoogleTokenResponse {

        private String access_token;
        private String id_token;
        private String refresh_token;
        private String scope;
        private String token_type;
        private Long expires_in;
    }
}
