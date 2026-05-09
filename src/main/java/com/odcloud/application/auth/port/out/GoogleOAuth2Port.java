package com.odcloud.application.auth.port.out;

public interface GoogleOAuth2Port {

    String getToken(String code);

    GoogleUserInfo getUserInfo(String googleAccessToken);
}
