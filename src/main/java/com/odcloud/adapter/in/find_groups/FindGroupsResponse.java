package com.odcloud.adapter.in.find_groups;

import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;
import java.util.List;
import lombok.Builder;

@Builder
record FindGroupsResponse(
    List<GroupResponse> groups
) {

    public static FindGroupsResponse of(FindGroupsServiceResponse serviceResponse) {
        return FindGroupsResponse.builder()
            .groups(serviceResponse.groups().stream()
                .map(GroupResponse::of)
                .toList())
            .build();
    }

    @Builder
    record GroupResponse(
        String id,
        String ownerEmail,
        String description,
        String regDt,
        String status
    ) {
        public static GroupResponse of(FindGroupsServiceResponse.GroupResponseItem item) {
            return GroupResponse.builder()
                .id(item.id())
                .ownerEmail(item.ownerEmail())
                .description(item.description())
                .regDt(item.regDt())
                .status(item.status())
                .build();
        }
    }
}
