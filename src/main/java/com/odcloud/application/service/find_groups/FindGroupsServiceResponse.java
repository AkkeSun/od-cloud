package com.odcloud.application.service.find_groups;

import com.odcloud.domain.model.Group;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record FindGroupsServiceResponse(
    List<GroupResponseItem> groups
) {

    public static FindGroupsServiceResponse of( List<Group> groups , Map<String, String> groupStatusMap) {
        return FindGroupsServiceResponse.builder()
            .groups( groups.stream()
                .map(group -> GroupResponseItem.of(
                    group, groupStatusMap.get(group.getId())))
                .toList())
            .build();
    }

    @Builder
    public record GroupResponseItem(
        String id,
        String ownerEmail,
        String description,
        String regDt,
        String status
    ) {

        public static GroupResponseItem of(Group group, String status) {
            return GroupResponseItem.builder()
                .id(group.getId())
                .ownerEmail(group.getOwnerEmail())
                .description(group.getDescription())
                .regDt(group.getRegDt() != null ? group.getRegDt().toString() : null)
                .status(status)
                .build();
        }
    }
}
