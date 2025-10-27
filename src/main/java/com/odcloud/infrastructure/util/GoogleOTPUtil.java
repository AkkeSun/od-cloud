package com.odcloud.infrastructure.util;

import com.odcloud.domain.model.Account;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public class GoogleOTPUtil {

    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public static String createTwoFactorSecretKey(String username) {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static String getOtpAuthUrl(Account account) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(
            account.getTwoFactorSecret()).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("od-cloud", account.getUsername(), key);
    }

    public static boolean valid(String twoFactorSecret, int code) {
        return gAuth.authorize(twoFactorSecret, code);
    }
}