package com.odcloud.application.subscription.service.find_group_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Group;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupSubscriptionsResponseTest {

    @Nested
    @DisplayName("[of] 그룹 목록과 구독 상세 목록으로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] 각 그룹의 구독이 올바르게 매핑된다")
        void success_withSubscriptions() {
            // given
            LocalDate nextBillingDate = LocalDate.of(2025, 12, 31);

            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("마케팅팀").build()
            );

            List<SubscriptionDetail> details = List.of(
                new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", nextBillingDate),
                new SubscriptionDetail("개발팀", "CLOUD_50GB", "김철수", null),
                new SubscriptionDetail("마케팅팀", "CLOUD_100GB", "이영희", nextBillingDate)
            );

            // when
            FindGroupSubscriptionsResponse response = FindGroupSubscriptionsResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupSubscriptionsResponse.GroupSubscriptions devGroup = response.groups().get(0);
            assertThat(devGroup.groupName()).isEqualTo("개발팀");
            assertThat(devGroup.subscriptions()).hasSize(2);
            assertThat(devGroup.subscriptions().get(0).productName()).isEqualTo("CLOUD_100GB");
            assertThat(devGroup.subscriptions().get(0).buyer()).isEqualTo("홍길동");
            assertThat(devGroup.subscriptions().get(0).nextBillingDate()).isNotNull();
            assertThat(devGroup.subscriptions().get(1).productName()).isEqualTo("CLOUD_50GB");
            assertThat(devGroup.subscriptions().get(1).nextBillingDate()).isNull();

            FindGroupSubscriptionsResponse.GroupSubscriptions marketingGroup = response.groups().get(1);
            assertThat(marketingGroup.groupName()).isEqualTo("마케팅팀");
            assertThat(marketingGroup.subscriptions()).hasSize(1);
            assertThat(marketingGroup.subscriptions().get(0).buyer()).isEqualTo("이영희");
        }

        @Test
        @DisplayName("[success] 구독이 없는 그룹은 빈 subscriptions 리스트를 가진다")
        void success_groupWithNoSubscriptions() {
            // given
            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("구독없는팀").build()
            );

            List<SubscriptionDetail> details = List.of(
                new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", null)
            );

            // when
            FindGroupSubscriptionsResponse response = FindGroupSubscriptionsResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupSubscriptionsResponse.GroupSubscriptions groupWithSubscription = response.groups().get(0);
            assertThat(groupWithSubscription.groupName()).isEqualTo("개발팀");
            assertThat(groupWithSubscription.subscriptions()).hasSize(1);

            FindGroupSubscriptionsResponse.GroupSubscriptions groupWithoutSubscription = response.groups().get(1);
            assertThat(groupWithoutSubscription.groupName()).isEqualTo("구독없는팀");
            assertThat(groupWithoutSubscription.subscriptions()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹 목록이 비어있으면 빈 groups 리스트를 반환한다")
        void success_emptyGroups() {
            // given
            List<Group> groups = List.of();
            List<SubscriptionDetail> details = List.of();

            // when
            FindGroupSubscriptionsResponse response = FindGroupSubscriptionsResponse.of(groups, details);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 구독 목록이 비어있으면 모든 그룹이 빈 subscriptions 리스트를 가진다")
        void success_emptySubscriptions() {
            // given
            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("마케팅팀").build()
            );
            List<SubscriptionDetail> details = List.of();

            // when
            FindGroupSubscriptionsResponse response = FindGroupSubscriptionsResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).subscriptions()).isEmpty();
            assertThat(response.groups().get(1).subscriptions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[SubscriptionItem.of] SubscriptionDetail로 SubscriptionItem 생성")
    class Describe_SubscriptionItem_of {

        @Test
        @DisplayName("[success] nextBillingDate가 있는 경우 문자열로 변환된다")
        void success_withNextBillingDate() {
            // given
            LocalDate nextBillingDate = LocalDate.of(2025, 12, 31);
            SubscriptionDetail detail = new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", nextBillingDate);

            // when
            FindGroupSubscriptionsResponse.SubscriptionItem item = FindGroupSubscriptionsResponse.SubscriptionItem.of(detail);

            // then
            assertThat(item.productName()).isEqualTo("CLOUD_100GB");
            assertThat(item.buyer()).isEqualTo("홍길동");
            assertThat(item.nextBillingDate()).isEqualTo(nextBillingDate.toString());
        }

        @Test
        @DisplayName("[success] nextBillingDate가 null인 경우 null이다")
        void success_nullNextBillingDate() {
            // given
            SubscriptionDetail detail = new SubscriptionDetail("개발팀", "CLOUD_100GB", "홍길동", null);

            // when
            FindGroupSubscriptionsResponse.SubscriptionItem item = FindGroupSubscriptionsResponse.SubscriptionItem.of(detail);

            // then
            assertThat(item.productName()).isEqualTo("CLOUD_100GB");
            assertThat(item.buyer()).isEqualTo("홍길동");
            assertThat(item.nextBillingDate()).isNull();
        }
    }
}
