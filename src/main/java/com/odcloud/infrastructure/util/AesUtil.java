package com.odcloud.infrastructure.util;

public interface AesUtil {

    String encryptText(String plainText);

    String decryptText(String encryptedText);
}
