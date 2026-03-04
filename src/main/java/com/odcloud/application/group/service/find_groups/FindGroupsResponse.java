package com.odcloud.application.group.service.find_groups;

import com.odcloud.domain.model.Group;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupsResponse(
    List<GroupResponseItem> groups
) {

    public static FindGroupsResponse of(List<Group> groups) {
        return FindGroupsResponse.builder()
            .groups(groups.stream()
                .map(GroupResponseItem::of)
                .toList())
            .build();
    }

    @Builder
    public record GroupResponseItem(
        Long id,
        String name,
        String ownerEmail,
        String regDt
    ) {

        public static GroupResponseItem of(Group group) {
            return GroupResponseItem.builder()
                .id(group.getId())
                .name(group.getName())
                .ownerEmail(group.getOwnerEmail())
                .regDt(group.getRegDt() != null ? group.getRegDt().toString() : null)
                .build();
        }
    }
}
