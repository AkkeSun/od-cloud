package com.odcloud.application.port.out;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;

public interface GoogleOAuth2Port {

    GoogleUserInfoResponse getUserInfo(String googleAccessToken);
}
