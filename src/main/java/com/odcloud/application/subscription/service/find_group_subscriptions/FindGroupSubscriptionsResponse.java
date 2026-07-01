package com.odcloud.application.subscription.service.find_group_subscriptions;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.domain.model.Group;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FindGroupSubscriptionsResponse(
    List<GroupSubscriptions> groups
) {

    public static FindGroupSubscriptionsResponse of(List<Group> groups, List<SubscriptionDetail> details) {
        Map<String, List<SubscriptionItem>> subscriptionsByGroup = details.stream()
            .collect(Collectors.groupingBy(
                SubscriptionDetail::groupName,
                Collectors.mapping(SubscriptionItem::of, Collectors.toList())
            ));

        List<GroupSubscriptions> result = groups.stream()
            .map(group -> GroupSubscriptions.builder()
                .groupName(group.getName())
                .subscriptions(subscriptionsByGroup.getOrDefault(group.getName(), List.of()))
                .build())
            .toList();

        return FindGroupSubscriptionsResponse.builder()
            .groups(result)
            .build();
    }

    @Builder
    public record GroupSubscriptions(
        String groupName,
        List<SubscriptionItem> subscriptions
    ) {

    }

    @Builder
    public record SubscriptionItem(
        String productName,
        String buyer,
        String nextBillingDate
    ) {

        static SubscriptionItem of(SubscriptionDetail detail) {
            return SubscriptionItem.builder()
                .productName(detail.productName())
                .buyer(detail.buyerNickname())
                .nextBillingDate(detail.nextBillingDate() != null ? detail.nextBillingDate().toString() : null)
                .build();
        }
    }
}
