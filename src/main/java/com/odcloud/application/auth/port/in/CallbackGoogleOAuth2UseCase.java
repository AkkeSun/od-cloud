package com.odcloud.application.auth.port.in;

public interface CallbackGoogleOAuth2UseCase {

    String callback(String code);
}
