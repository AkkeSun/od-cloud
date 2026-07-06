package com.odcloud.application.subscription.service.find_group_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupSubscriptionsResponseTest {

    @Nested
    @DisplayName("[of] 구독 상세 목록으로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] 구독이 상품별로 올바르게 매핑된다")
        void success_withSubscriptions() {
            // given
            LocalDate expiredDate = LocalDate.of(2026, 8, 1);
            List<SubscriptionDetail> details = List.of(
                new SubscriptionDetail(100L, "CLOUD_100GB", 1000L, 1L, "개발팀", "홍길동", "ACTIVE", expiredDate),
                new SubscriptionDetail(50L, "CLOUD_50GB", 1001L, 1L, "개발팀", "홍길동", "ACTIVE", expiredDate),
                new SubscriptionDetail(100L, "CLOUD_100GB", 1002L, 2L, "마케팅팀", "이영희", "EXP_PENDING", expiredDate)
            );

            // when
            List<FindGroupSubscriptionsResponse> response = FindGroupSubscriptionsResponse.of(details);

            // then
            assertThat(response).hasSize(2);

            FindGroupSubscriptionsResponse cloud100 = response.get(0);
            assertThat(cloud100.productId()).isEqualTo(100L);
            assertThat(cloud100.productName()).isEqualTo("CLOUD_100GB");
            assertThat(cloud100.groups()).hasSize(2);
            assertThat(cloud100.groups().get(0).groupId()).isEqualTo(1L);
            assertThat(cloud100.groups().get(0).groupName()).isEqualTo("개발팀");
            assertThat(cloud100.groups().get(0).subscriptionId()).isEqualTo(1000L);
            assertThat(cloud100.groups().get(0).buyer()).isEqualTo("홍길동");
            assertThat(cloud100.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(cloud100.groups().get(0).expiredDate()).isEqualTo(expiredDate);
            assertThat(cloud100.groups().get(1).groupName()).isEqualTo("마케팅팀");
            assertThat(cloud100.groups().get(1).status()).isEqualTo("EXP_PENDING");

            FindGroupSubscriptionsResponse cloud50 = response.get(1);
            assertThat(cloud50.productId()).isEqualTo(50L);
            assertThat(cloud50.productName()).isEqualTo("CLOUD_50GB");
            assertThat(cloud50.groups()).hasSize(1);
            assertThat(cloud50.groups().get(0).groupName()).isEqualTo("개발팀");
        }

        @Test
        @DisplayName("[success] 같은 그룹에 동일 상품의 ACTIVE, EXP_PENDING 구독이 함께 조회되면 ACTIVE 만 응답한다")
        void success_dedupesActiveOverExpPendingInSameGroup() {
            // given
            LocalDate expiredDate = LocalDate.of(2026, 8, 1);
            List<SubscriptionDetail> details = List.of(
                new SubscriptionDetail(100L, "CLOUD_100GB", 2000L, 1L, "개발팀", "홍길동", "EXP_PENDING", expiredDate),
                new SubscriptionDetail(100L, "CLOUD_100GB", 2001L, 1L, "개발팀", "김철수", "ACTIVE", expiredDate)
            );

            // when
            List<FindGroupSubscriptionsResponse> response = FindGroupSubscriptionsResponse.of(details);

            // then
            assertThat(response).hasSize(1);
            FindGroupSubscriptionsResponse cloud100 = response.get(0);
            assertThat(cloud100.groups()).hasSize(1);
            assertThat(cloud100.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(cloud100.groups().get(0).subscriptionId()).isEqualTo(2001L);
        }

        @Test
        @DisplayName("[success] 구독 목록이 비어있으면 빈 리스트를 반환한다")
        void success_emptySubscriptions() {
            // given
            List<SubscriptionDetail> details = List.of();

            // when
            List<FindGroupSubscriptionsResponse> response = FindGroupSubscriptionsResponse.of(details);

            // then
            assertThat(response).isEmpty();
        }

        @Test
        @DisplayName("[success] 구독중인 그룹이 없는 상품은 productName 만 응답하고 groups 는 빈 리스트다")
        void success_productWithoutSubscription() {
            // given
            List<SubscriptionDetail> details = List.of(
                new SubscriptionDetail(100L, "CLOUD_100GB", null, null, null, null, null, null)
            );

            // when
            List<FindGroupSubscriptionsResponse> response = FindGroupSubscriptionsResponse.of(details);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).productId()).isEqualTo(100L);
            assertThat(response.get(0).productName()).isEqualTo("CLOUD_100GB");
            assertThat(response.get(0).groups()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[GroupInfo.of] SubscriptionDetail로 GroupInfo 생성")
    class Describe_GroupInfo_of {

        @Test
        @DisplayName("[success] SubscriptionDetail의 필드가 그대로 매핑된다")
        void success() {
            // given
            LocalDate expiredDate = LocalDate.of(2026, 8, 1);
            SubscriptionDetail detail =
                new SubscriptionDetail(100L, "CLOUD_100GB", 5000L, 1L, "개발팀", "홍길동", "ACTIVE", expiredDate);

            // when
            FindGroupSubscriptionsResponse.GroupInfo groupInfo = FindGroupSubscriptionsResponse.GroupInfo.of(detail);

            // then
            assertThat(groupInfo.groupId()).isEqualTo(1L);
            assertThat(groupInfo.groupName()).isEqualTo("개발팀");
            assertThat(groupInfo.subscriptionId()).isEqualTo(5000L);
            assertThat(groupInfo.buyer()).isEqualTo("홍길동");
            assertThat(groupInfo.status()).isEqualTo("ACTIVE");
            assertThat(groupInfo.expiredDate()).isEqualTo(expiredDate);
        }
    }
}
