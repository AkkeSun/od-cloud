package com.odcloud.application.subscription.service.modify_subscription_plan;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifySubscriptionPlanCommandTest {

    @Nested
    @DisplayName("[builder] ModifySubscriptionPlanCommand를 생성하는 빌더")
    class Describe_builder {

        @Test
        @DisplayName("[success] account, currentSubscriptionId, newProductId로 Command를 생성한다")
        void success() {
            // given
            Account account = Account.builder().id(1L).build();

            // when
            ModifySubscriptionPlanCommand command = ModifySubscriptionPlanCommand.builder()
                .account(account)
                .currentSubscriptionId(10L)
                .newProductId(20L)
                .build();

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.currentSubscriptionId()).isEqualTo(10L);
            assertThat(command.newProductId()).isEqualTo(20L);
        }
    }
}
