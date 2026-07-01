package com.odcloud.application.subscription.service.find_group_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupSubscriptionsServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private FindGroupSubscriptionsService findGroupSubscriptionsService;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        findGroupSubscriptionsService = new FindGroupSubscriptionsService(fakeSubscriptionStoragePort);
    }

    @Nested
    @DisplayName("[find] 계정의 그룹별 활성 구독 조회")
    class Describe_find {

        @Test
        @DisplayName("[success] 각 그룹의 활성 구독이 그룹별로 조회된다")
        void success_withSubscriptions() {
            // given
            LocalDate nextBillingDate = LocalDate.of(2025, 12, 31);

            Group groupA = Group.builder().id(1L).name("개발팀").build();
            Group groupB = Group.builder().id(2L).name("마케팅팀").build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(groupA, groupB))
                .build();

            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", nextBillingDate));
            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail("마케팅팀", "CLOUD_50GB", "김철수", null));

            // when
            FindGroupSubscriptionsResponse response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupSubscriptionsResponse.GroupSubscriptions devGroup = response.groups().get(0);
            assertThat(devGroup.groupName()).isEqualTo("개발팀");
            assertThat(devGroup.subscriptions()).hasSize(1);
            assertThat(devGroup.subscriptions().get(0).productName()).isEqualTo("CLOUD_100GB");

            FindGroupSubscriptionsResponse.GroupSubscriptions marketingGroup = response.groups().get(1);
            assertThat(marketingGroup.groupName()).isEqualTo("마케팅팀");
            assertThat(marketingGroup.subscriptions()).hasSize(1);
            assertThat(marketingGroup.subscriptions().get(0).productName()).isEqualTo("CLOUD_50GB");
        }

        @Test
        @DisplayName("[success] 구독이 없는 그룹은 빈 subscriptions 리스트로 응답된다")
        void success_groupWithNoSubscriptions() {
            // given
            Group groupA = Group.builder().id(1L).name("개발팀").build();
            Group groupB = Group.builder().id(2L).name("구독없는팀").build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(groupA, groupB))
                .build();

            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", null));

            // when
            FindGroupSubscriptionsResponse response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);

            assertThat(response.groups().get(0).groupName()).isEqualTo("개발팀");
            assertThat(response.groups().get(0).subscriptions()).hasSize(1);

            assertThat(response.groups().get(1).groupName()).isEqualTo("구독없는팀");
            assertThat(response.groups().get(1).subscriptions()).isEmpty();
        }

        @Test
        @DisplayName("[success] 가입된 그룹이 없으면 빈 groups 리스트를 반환한다")
        void success_noGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of())
                .build();

            // when
            FindGroupSubscriptionsResponse response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹은 있지만 활성 구독이 전혀 없으면 모든 그룹이 빈 subscriptions 리스트를 가진다")
        void success_allGroupsWithNoSubscriptions() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(
                    Group.builder().id(1L).name("개발팀").build(),
                    Group.builder().id(2L).name("마케팅팀").build()
                ))
                .build();

            // when
            FindGroupSubscriptionsResponse response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).subscriptions()).isEmpty();
            assertThat(response.groups().get(1).subscriptions()).isEmpty();
        }
    }
}
