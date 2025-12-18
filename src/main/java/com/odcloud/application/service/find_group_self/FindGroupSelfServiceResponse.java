package com.odcloud.application.service.find_group_self;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupSelfServiceResponse(
    List<ActiveGroupInfo> activeGroups,
    List<PendingGroupInfo> pendingGroups
) {

    public static FindGroupSelfServiceResponse of(List<ActiveGroupInfo> activeGroups,
        List<PendingGroupInfo> pendingGroups) {
        return FindGroupSelfServiceResponse.builder()
            .activeGroups(activeGroups)
            .pendingGroups(pendingGroups)
            .build();
    }

    @Builder
    public record ActiveGroupInfo(
        String id,
        String name,
        MemberInfo manager,
        List<MemberInfo> members,
        int activeMemberCount
    ) {

        public static ActiveGroupInfo of(Group group) {
            MemberInfo managerInfo = group.getGroupMembers().stream()
                .filter(member -> member.getEmail().equals(group.getOwnerEmail()))
                .findFirst()
                .map(MemberInfo::of).get();

            List<MemberInfo> memberInfos = group.getGroupMembers().stream()
                .filter(member -> "ACTIVE".equals(member.getStatus()))
                .filter(member -> !member.getEmail().equals(group.getOwnerEmail()))
                .map(MemberInfo::of)
                .toList();

            return ActiveGroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .manager(managerInfo)
                .members(memberInfos)
                .activeMemberCount(memberInfos.size() + 1)
                .build();
        }
    }

    @Builder
    public record PendingGroupInfo(
        String id,
        String name,
        int activeMemberCount
    ) {

        public static PendingGroupInfo of(Group group) {
            return PendingGroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .activeMemberCount((int) group.getGroupMembers().stream()
                    .filter(member -> "ACTIVE".equals(member.getStatus()))
                    .count())
                .build();
        }
    }

    @Builder
    public record MemberInfo(
        String nickname,
        String email
    ) {

        public static MemberInfo of(GroupAccount groupAccount) {
            return MemberInfo.builder()
                .nickname(groupAccount.getNickName())
                .email(groupAccount.getEmail())
                .build();
        }
    }
}
