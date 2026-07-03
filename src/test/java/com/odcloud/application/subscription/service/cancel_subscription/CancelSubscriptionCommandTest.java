package com.odcloud.application.subscription.service.cancel_subscription;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CancelSubscriptionCommandTest {

    @Nested
    @DisplayName("[builder] CancelSubscriptionCommand를 생성하는 빌더")
    class Describe_builder {

        @Test
        @DisplayName("[success] subscriptionId와 account로 Command를 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .build();

            // when
            CancelSubscriptionCommand command = CancelSubscriptionCommand.builder()
                .subscriptionId(10L)
                .account(account)
                .build();

            // then
            assertThat(command.subscriptionId()).isEqualTo(10L);
            assertThat(command.account()).isEqualTo(account);
        }
    }
}
