package com.odcloud.application.subscription.service.cancel_subscription;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CancelSubscriptionResponseTest {

    @Nested
    @DisplayName("[ofSuccess] 취소 성공 응답을 생성하는 정적 팩토리 메서드")
    class Describe_ofSuccess {

        @Test
        @DisplayName("[success] result가 true인 응답을 생성한다")
        void success() {
            // when
            CancelSubscriptionResponse response = CancelSubscriptionResponse.ofSuccess();

            // then
            assertThat(response.result()).isTrue();
        }
    }
}
