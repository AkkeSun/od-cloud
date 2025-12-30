package com.odcloud.adapter.in.controller.group.find_groups;

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
        String name,
        String ownerEmail,
        String regDt
    ) {
        public static GroupResponse of(FindGroupsServiceResponse.GroupResponseItem item) {
            return GroupResponse.builder()
                .id(item.id())
                .name(item.name())
                .ownerEmail(item.ownerEmail())
                .regDt(item.regDt())
                .build();
        }
    }
}
