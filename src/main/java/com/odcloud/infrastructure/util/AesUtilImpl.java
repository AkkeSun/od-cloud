package com.odcloud.infrastructure.util;

import com.odcloud.infrastructure.constant.ProfileConstant;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AesUtilImpl implements AesUtil {

    private final ProfileConstant constant;

    @Override
    public String encryptText(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");

            byte[] key = new byte[16];
            int i = 0;

            for (byte b : constant.aesSecretKey().getBytes()) {
                key[i++ % 16] ^= b;
            }

            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return new String(Hex.encodeHex(cipher.doFinal(plainText.getBytes(
                StandardCharsets.UTF_8)))).toUpperCase();
        } catch (Exception e) {
            return plainText;
        }
    }

    @Override
    public String decryptText(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");

            byte[] key = new byte[16];
            int i = 0;

            for (byte b : constant.aesSecretKey().getBytes()) {
                key[i++ % 16] ^= b;
            }

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] decodedHex = Hex.decodeHex(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedHex);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedText;
        }
    }
}
