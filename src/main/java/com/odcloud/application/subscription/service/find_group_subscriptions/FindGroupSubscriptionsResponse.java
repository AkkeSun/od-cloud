package com.odcloud.application.subscription.service.find_group_subscriptions;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FindGroupSubscriptionsResponse(
    Long productId,
    String productName,
    List<GroupInfo> groups
) {

    public static List<FindGroupSubscriptionsResponse> of(List<SubscriptionDetail> details) {
        Map<Long, List<SubscriptionDetail>> detailsByProduct = details.stream()
            .collect(Collectors.groupingBy(
                SubscriptionDetail::productId,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return detailsByProduct.values().stream()
            .map(productDetails -> FindGroupSubscriptionsResponse.builder()
                .productId(productDetails.get(0).productId())
                .productName(productDetails.get(0).productName())
                .groups(toGroupInfos(productDetails))
                .build())
            .toList();
    }

    private static List<GroupInfo> toGroupInfos(List<SubscriptionDetail> details) {
        Map<Long, SubscriptionDetail> detailByGroupId = new LinkedHashMap<>();
        for (SubscriptionDetail detail : details) {
            if (detail.groupId() == null) {
                continue;
            }
            detailByGroupId.merge(detail.groupId(), detail,
                (existing, incoming) -> "ACTIVE".equals(existing.status()) ? existing : incoming);
        }
        return detailByGroupId.values().stream()
            .map(GroupInfo::of)
            .toList();
    }

    @Builder
    public record GroupInfo(
        Long groupId,
        String groupName,
        Long subscriptionId,
        String buyer,
        String status,
        LocalDate expiredDate
    ) {

        static GroupInfo of(SubscriptionDetail detail) {
            return GroupInfo.builder()
                .groupId(detail.groupId())
                .groupName(detail.groupName())
                .subscriptionId(detail.subscriptionId())
                .buyer(detail.buyerNickname())
                .status(detail.status())
                .expiredDate(detail.expiredDate())
                .build();
        }
    }
}
