package com.odcloud.application.subscription.service.find_group_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import java.time.LocalDateTime;
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
        @DisplayName("[success] 활성 구독이 상품별로 조회된다")
        void success_withSubscriptions() {
            // given
            LocalDateTime expiredDate = LocalDateTime.of(2026, 8, 1, 0, 0);
            Group groupA = Group.builder().id(1L).name("개발팀").build();
            Group groupB = Group.builder().id(2L).name("마케팅팀").build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(groupA, groupB))
                .build();

            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail(1L, "개발팀", "CLOUD_100GB", 10L, "홍길동", "ACTIVE", expiredDate));
            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail(2L, "마케팅팀", "CLOUD_50GB", 20L, "김철수", "ACTIVE", expiredDate));

            // when
            List<FindGroupSubscriptionsResponse> response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response).hasSize(2);

            FindGroupSubscriptionsResponse cloud100 = response.get(0);
            assertThat(cloud100.productName()).isEqualTo("CLOUD_100GB");
            assertThat(cloud100.groups()).hasSize(1);
            assertThat(cloud100.groups().get(0).groupId()).isEqualTo(1L);
            assertThat(cloud100.groups().get(0).groupName()).isEqualTo("개발팀");
            assertThat(cloud100.groups().get(0).buyerId()).isEqualTo(10L);
            assertThat(cloud100.groups().get(0).buyer()).isEqualTo("홍길동");
            assertThat(cloud100.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(cloud100.groups().get(0).expiredDate()).isEqualTo(expiredDate);

            FindGroupSubscriptionsResponse cloud50 = response.get(1);
            assertThat(cloud50.productName()).isEqualTo("CLOUD_50GB");
            assertThat(cloud50.groups()).hasSize(1);
            assertThat(cloud50.groups().get(0).groupName()).isEqualTo("마케팅팀");
        }

        @Test
        @DisplayName("[success] 같은 상품을 여러 그룹이 구독중이면 하나의 상품에 그룹이 모인다")
        void success_multipleGroupsForSameProduct() {
            // given
            LocalDateTime expiredDate = LocalDateTime.of(2026, 8, 1, 0, 0);
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(
                    Group.builder().id(1L).name("개발팀").build(),
                    Group.builder().id(2L).name("마케팅팀").build()
                ))
                .build();

            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail(1L, "개발팀", "CLOUD_100GB", 10L, "홍길동", "ACTIVE", expiredDate));
            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail(2L, "마케팅팀", "CLOUD_100GB", 20L, "김철수", "EXP_PENDING", expiredDate));

            // when
            List<FindGroupSubscriptionsResponse> response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).productName()).isEqualTo("CLOUD_100GB");
            assertThat(response.get(0).groups()).hasSize(2);
        }

        @Test
        @DisplayName("[success] 가입된 그룹이 없으면 빈 리스트를 반환한다")
        void success_noGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of())
                .build();

            // when
            List<FindGroupSubscriptionsResponse> response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹은 있지만 활성 구독이 전혀 없어도 상품명은 응답하고 groups 는 빈 리스트다")
        void success_noSubscriptions() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(
                    Group.builder().id(1L).name("개발팀").build(),
                    Group.builder().id(2L).name("마케팅팀").build()
                ))
                .build();

            fakeSubscriptionStoragePort.database.add(
                new SubscriptionDetail(null, null, "CLOUD_100GB", null, null, null, null));

            // when
            List<FindGroupSubscriptionsResponse> response = findGroupSubscriptionsService.find(account);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).productName()).isEqualTo("CLOUD_100GB");
            assertThat(response.get(0).groups()).isEmpty();
        }
    }
}
