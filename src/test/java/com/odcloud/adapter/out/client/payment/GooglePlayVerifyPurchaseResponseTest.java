package com.odcloud.adapter.out.client.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GooglePlayVerifyPurchaseResponseTest {

    @Nested
    @DisplayName("[isValid] 유효성 검증 메서드")
    class Describe_isValid {

        @Test
        @DisplayName("[success] purchaseState가 0이면 true를 반환한다 (Purchased)")
        void success_purchaseStateZero_returnsTrue() {
            // given
            GooglePlayVerifyPurchaseResponse response = createResponse(0);

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] purchaseState가 1이면 false를 반환한다 (Canceled)")
        void success_purchaseStateOne_returnsFalse() {
            // given
            GooglePlayVerifyPurchaseResponse response = createResponse(1);

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] purchaseState가 2이면 false를 반환한다 (Pending)")
        void success_purchaseStateTwo_returnsFalse() {
            // given
            GooglePlayVerifyPurchaseResponse response = createResponse(2);

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] purchaseState가 null이면 false를 반환한다")
        void success_purchaseStateNull_returnsFalse() {
            // given
            GooglePlayVerifyPurchaseResponse response = createResponse(null);

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[record] 레코드 생성")
    class Describe_record {

        @Test
        @DisplayName("[success] 모든 필드를 지정하여 생성한다")
        void success_createWithAllFields() {
            // when
            GooglePlayVerifyPurchaseResponse response = new GooglePlayVerifyPurchaseResponse(
                "androidpublisher#productPurchase",
                1609459200000L,
                0,
                1,
                "developer-payload",
                "GPA.1234-5678-9012-34567",
                0,
                1,
                "purchase-token",
                "com.odcloud.storage_basic",
                1,
                "obfuscated-account-id",
                "obfuscated-profile-id",
                "KR"
            );

            // then
            assertThat(response.kind()).isEqualTo("androidpublisher#productPurchase");
            assertThat(response.purchaseTimeMillis()).isEqualTo(1609459200000L);
            assertThat(response.purchaseState()).isEqualTo(0);
            assertThat(response.consumptionState()).isEqualTo(1);
            assertThat(response.developerPayload()).isEqualTo("developer-payload");
            assertThat(response.orderId()).isEqualTo("GPA.1234-5678-9012-34567");
            assertThat(response.purchaseType()).isEqualTo(0);
            assertThat(response.acknowledgementState()).isEqualTo(1);
            assertThat(response.purchaseToken()).isEqualTo("purchase-token");
            assertThat(response.productId()).isEqualTo("com.odcloud.storage_basic");
            assertThat(response.quantity()).isEqualTo(1);
            assertThat(response.obfuscatedExternalAccountId()).isEqualTo("obfuscated-account-id");
            assertThat(response.obfuscatedExternalProfileId()).isEqualTo("obfuscated-profile-id");
            assertThat(response.regionCode()).isEqualTo("KR");
        }

        @Test
        @DisplayName("[success] 최소 필드로 생성한다")
        void success_createWithMinimalFields() {
            // when
            GooglePlayVerifyPurchaseResponse response = new GooglePlayVerifyPurchaseResponse(
                null,
                null,
                0,
                null,
                null,
                "GPA.1234",
                null,
                null,
                "token",
                "product-id",
                null,
                null,
                null,
                null
            );

            // then
            assertThat(response.purchaseState()).isEqualTo(0);
            assertThat(response.orderId()).isEqualTo("GPA.1234");
            assertThat(response.isValid()).isTrue();
        }
    }

    private GooglePlayVerifyPurchaseResponse createResponse(Integer purchaseState) {
        return new GooglePlayVerifyPurchaseResponse(
            "androidpublisher#productPurchase",
            System.currentTimeMillis(),
            purchaseState,
            1,
            null,
            "GPA.1234-5678-9012-34567",
            0,
            1,
            "purchase-token",
            "com.odcloud.storage_basic",
            1,
            null,
            null,
            "KR"
        );
    }
}
