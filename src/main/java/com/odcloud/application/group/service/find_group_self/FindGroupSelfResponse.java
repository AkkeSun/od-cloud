package com.odcloud.application.group.service.find_group_self;

import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupSelfResponse(
    List<ActiveGroupInfo> activeGroups,
    List<PendingGroupInfo> pendingGroups,
    List<DeniedGroupInfo> deniedGroups
) {

    public static FindGroupSelfResponse of(List<ActiveGroupInfo> activeGroups,
        List<PendingGroupInfo> pendingGroups,
        List<DeniedGroupInfo> deniedGroups) {
        return FindGroupSelfResponse.builder()
            .activeGroups(activeGroups)
            .pendingGroups(pendingGroups)
            .deniedGroups(deniedGroups)
            .build();
    }

    @Builder
    public record ActiveGroupInfo(
        Long id,
        String name,
        Long folderId,
        MemberInfo manager,
        int activeMemberCount
    ) {

        public static ActiveGroupInfo of(Group group, FolderInfo rootFolder) {
            return ActiveGroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .folderId(rootFolder != null ? rootFolder.getId() : null)
                .manager(group.getGroupMembers().stream()
                    .filter(member -> member.getEmail().equals(group.getOwnerEmail()))
                    .findFirst()
                    .map(MemberInfo::of).get())
                .activeMemberCount((int) group.getGroupMembers().stream()
                    .filter(member -> "ACTIVE".equals(member.getStatus()))
                    .count())
                .build();
        }
    }

    @Builder
    public record PendingGroupInfo(
        Long id,
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
    public record DeniedGroupInfo(
        Long id,
        String name,
        String deniedCause
    ) {

        public static DeniedGroupInfo of(Group group, GroupAccount groupAccount) {
            return DeniedGroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .deniedCause(groupAccount.getMemo())
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
