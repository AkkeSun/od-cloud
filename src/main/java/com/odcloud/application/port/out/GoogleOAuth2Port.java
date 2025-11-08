package com.odcloud.application.port.out;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;

public interface GoogleOAuth2Port {

    GoogleTokenResponse getToken(String code);

    GoogleUserInfoResponse getUserInfo(String googleAccessToken);
}
