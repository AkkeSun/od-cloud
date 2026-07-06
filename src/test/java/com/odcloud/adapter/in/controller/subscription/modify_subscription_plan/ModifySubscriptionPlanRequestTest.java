package com.odcloud.adapter.in.controller.subscription.modify_subscription_plan;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifySubscriptionPlanRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메소드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] account와 요청 필드를 포함한 Command로 변환한다")
        void success() {
            // given
            Account account = Account.builder().id(1L).build();
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(10L)
                .newProductId(20L)
                .build();

            // when
            ModifySubscriptionPlanCommand command = request.toCommand(account);

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.currentSubscriptionId()).isEqualTo(10L);
            assertThat(command.newProductId()).isEqualTo(20L);
        }
    }

    @Nested
    @DisplayName("[toString] Request를 문자열로 변환하는 메소드")
    class Describe_toString {

        @Test
        @DisplayName("[success] JSON 문자열을 반환한다")
        void success() {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(10L)
                .newProductId(20L)
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).contains("10").contains("20");
        }
    }
}
