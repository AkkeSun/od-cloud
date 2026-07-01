package com.odcloud.application.subscription.service.find_group_subscriptions;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FindGroupSubscriptionsResponse(
    String productName,
    List<GroupInfo> groups
) {

    public static List<FindGroupSubscriptionsResponse> of(List<SubscriptionDetail> details) {
        Map<String, List<GroupInfo>> groupsByProduct = details.stream()
            .collect(Collectors.groupingBy(
                SubscriptionDetail::productName,
                LinkedHashMap::new,
                Collectors.filtering(
                    detail -> detail.groupId() != null,
                    Collectors.mapping(GroupInfo::of, Collectors.toList())
                )
            ));

        return groupsByProduct.entrySet().stream()
            .map(entry -> FindGroupSubscriptionsResponse.builder()
                .productName(entry.getKey())
                .groups(entry.getValue())
                .build())
            .toList();
    }

    @Builder
    public record GroupInfo(
        Long groupId,
        String groupName,
        Long buyerId,
        String buyer,
        String status
    ) {

        static GroupInfo of(SubscriptionDetail detail) {
            return GroupInfo.builder()
                .groupId(detail.groupId())
                .groupName(detail.groupName())
                .buyerId(detail.buyerId())
                .buyer(detail.buyerNickname())
                .status(detail.status())
                .build();
        }
    }
}
