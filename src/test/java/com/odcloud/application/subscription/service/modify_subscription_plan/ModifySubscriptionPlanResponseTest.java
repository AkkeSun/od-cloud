package com.odcloud.application.subscription.service.modify_subscription_plan;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifySubscriptionPlanResponseTest {

    @Nested
    @DisplayName("[ofUpgrade] 업그레이드 성공 응답을 생성하는 정적 팩토리 메서드")
    class Describe_ofUpgrade {

        @Test
        @DisplayName("[success] changeType이 UPGRADE이고 결제 정보를 포함한 응답을 생성한다")
        void success() {
            // when
            ModifySubscriptionPlanResponse response =
                ModifySubscriptionPlanResponse.ofUpgrade(1L, 2L, 3L, BigDecimal.valueOf(15000));

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.changeType()).isEqualTo("UPGRADE");
            assertThat(response.previousSubscriptionId()).isEqualTo(1L);
            assertThat(response.newSubscriptionId()).isEqualTo(2L);
            assertThat(response.paymentId()).isEqualTo(3L);
            assertThat(response.chargedAmount()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        }
    }

    @Nested
    @DisplayName("[ofDowngrade] 다운그레이드 성공 응답을 생성하는 정적 팩토리 메서드")
    class Describe_ofDowngrade {

        @Test
        @DisplayName("[success] changeType이 DOWNGRADE이고 결제 정보가 없는 응답을 생성한다")
        void success() {
            // when
            ModifySubscriptionPlanResponse response =
                ModifySubscriptionPlanResponse.ofDowngrade(1L, 2L);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.changeType()).isEqualTo("DOWNGRADE");
            assertThat(response.previousSubscriptionId()).isEqualTo(1L);
            assertThat(response.newSubscriptionId()).isEqualTo(2L);
            assertThat(response.paymentId()).isNull();
            assertThat(response.chargedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
