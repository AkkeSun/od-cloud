package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.constant.ProfileConstant.Jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AesUtilImplTest {

    private AesUtilImpl aesUtil;
    private ProfileConstant profileConstant;

    @BeforeEach
    void setUp() {
        Jwt jwt = Jwt.builder()
            .tempTokenTtl(300000L)
            .accessTokenTtl(1800000L)
            .refreshTokenTtl(604800000L)
            .secretKey("test-secret-key")
            .build();

        profileConstant = ProfileConstant.builder()
            .jwt(jwt)
            .aesSecretKey("test-aes-secret-key-for-encryption")
            .build();

        aesUtil = new AesUtilImpl(profileConstant);
    }

    @Nested
    @DisplayName("[encryptText] 텍스트 암호화")
    class Describe_encryptText {

        @Test
        @DisplayName("[success] 정상적으로 텍스트를 암호화한다")
        void success() {
            // given
            String plainText = "Hello World";

            // when
            String encryptedText = aesUtil.encryptText(plainText);

            // then
            assertThat(encryptedText).isNotNull();
            assertThat(encryptedText).isNotEqualTo(plainText);
            assertThat(encryptedText).isUpperCase();
            assertThat(encryptedText).matches("^[0-9A-F]+$");
        }

        @Test
        @DisplayName("[success] 빈 문자열을 암호화한다")
        void success_emptyString() {
            // given
            String plainText = "";

            // when
            String encryptedText = aesUtil.encryptText(plainText);

            // then
            assertThat(encryptedText).isNotNull();
        }

        @Test
        @DisplayName("[success] 한글 텍스트를 암호화한다")
        void success_koreanText() {
            // given
            String plainText = "안녕하세요";

            // when
            String encryptedText = aesUtil.encryptText(plainText);

            // then
            assertThat(encryptedText).isNotNull();
            assertThat(encryptedText).isNotEqualTo(plainText);
            assertThat(encryptedText).isUpperCase();
        }

        @Test
        @DisplayName("[success] 특수 문자를 포함한 텍스트를 암호화한다")
        void success_specialCharacters() {
            // given
            String plainText = "test@example.com!@#$%^&*()";

            // when
            String encryptedText = aesUtil.encryptText(plainText);

            // then
            assertThat(encryptedText).isNotNull();
            assertThat(encryptedText).isNotEqualTo(plainText);
        }

        @Test
        @DisplayName("[success] 긴 텍스트를 암호화한다")
        void success_longText() {
            // given
            String plainText = "This is a very long text that needs to be encrypted. ".repeat(10);

            // when
            String encryptedText = aesUtil.encryptText(plainText);

            // then
            assertThat(encryptedText).isNotNull();
            assertThat(encryptedText).isNotEqualTo(plainText);
        }

        @Test
        @DisplayName("[success] 동일한 텍스트를 암호화하면 항상 같은 결과를 반환한다")
        void success_consistentEncryption() {
            // given
            String plainText = "consistent text";

            // when
            String encrypted1 = aesUtil.encryptText(plainText);
            String encrypted2 = aesUtil.encryptText(plainText);

            // then
            assertThat(encrypted1).isEqualTo(encrypted2);
        }
    }

    @Nested
    @DisplayName("[decryptText] 텍스트 복호화")
    class Describe_decryptText {

        @Test
        @DisplayName("[success] 정상적으로 암호화된 텍스트를 복호화한다")
        void success() {
            // given
            String originalText = "Hello World";
            String encryptedText = aesUtil.encryptText(originalText);

            // when
            String decryptedText = aesUtil.decryptText(encryptedText);

            // then
            assertThat(decryptedText).isEqualTo(originalText);
        }

        @Test
        @DisplayName("[success] 한글 텍스트를 암호화하고 복호화한다")
        void success_koreanText() {
            // given
            String originalText = "안녕하세요";
            String encryptedText = aesUtil.encryptText(originalText);

            // when
            String decryptedText = aesUtil.decryptText(encryptedText);

            // then
            assertThat(decryptedText).isEqualTo(originalText);
        }

        @Test
        @DisplayName("[success] 특수 문자를 포함한 텍스트를 암호화하고 복호화한다")
        void success_specialCharacters() {
            // given
            String originalText = "test@example.com!@#$%^&*()";
            String encryptedText = aesUtil.encryptText(originalText);

            // when
            String decryptedText = aesUtil.decryptText(encryptedText);

            // then
            assertThat(decryptedText).isEqualTo(originalText);
        }

        @Test
        @DisplayName("[success] 긴 텍스트를 암호화하고 복호화한다")
        void success_longText() {
            // given
            String originalText = "This is a very long text that needs to be encrypted and decrypted. ".repeat(
                10);
            String encryptedText = aesUtil.encryptText(originalText);

            // when
            String decryptedText = aesUtil.decryptText(encryptedText);

            // then
            assertThat(decryptedText).isEqualTo(originalText);
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 암호화 텍스트를 복호화하면 원본을 반환한다")
        void failure_invalidEncryptedText() {
            // given
            String invalidEncryptedText = "invalid-encrypted-text";

            // when
            String result = aesUtil.decryptText(invalidEncryptedText);

            // then
            assertThat(result).isEqualTo(invalidEncryptedText);
        }

        @Test
        @DisplayName("[success] 빈 문자열을 암호화하고 복호화한다")
        void success_emptyString() {
            // given
            String originalText = "";
            String encryptedText = aesUtil.encryptText(originalText);

            // when
            String decryptedText = aesUtil.decryptText(encryptedText);

            // then
            assertThat(decryptedText).isEqualTo(originalText);
        }
    }

    @Nested
    @DisplayName("[encryptText & decryptText] 암호화 및 복호화 통합")
    class Describe_encryptAndDecrypt {

        @Test
        @DisplayName("[success] 여러 텍스트를 암호화하고 복호화하여 원본과 일치하는지 확인한다")
        void success_multipleTexts() {
            // given
            String[] testTexts = {
                "simple text",
                "test@example.com",
                "안녕하세요",
                "12345",
                "!@#$%^&*()",
                "Mixed 한글 and English 123"
            };

            for (String originalText : testTexts) {
                // when
                String encrypted = aesUtil.encryptText(originalText);
                String decrypted = aesUtil.decryptText(encrypted);

                // then
                assertThat(decrypted).isEqualTo(originalText);
            }
        }

        @Test
        @DisplayName("[success] 다른 텍스트는 다른 암호화 결과를 생성한다")
        void success_differentTextsDifferentEncryption() {
            // given
            String text1 = "Hello";
            String text2 = "World";

            // when
            String encrypted1 = aesUtil.encryptText(text1);
            String encrypted2 = aesUtil.encryptText(text2);

            // then
            assertThat(encrypted1).isNotEqualTo(encrypted2);
        }
    }
}
