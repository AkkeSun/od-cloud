package com.odcloud.adapter.in.controller.find_group_self;

import com.odcloud.application.service.find_group_self.FindGroupSelfServiceResponse;
import java.util.List;
import lombok.Builder;

@Builder
record FindGroupSelfResponse(
    List<ActiveGroupInfo> activeGroups,
    List<PendingGroupInfo> pendingGroups,
    List<DeniedGroupInfo> deniedGroups
) {

    public static FindGroupSelfResponse of(FindGroupSelfServiceResponse serviceResponse) {
        return FindGroupSelfResponse.builder()
            .activeGroups(serviceResponse.activeGroups().stream()
                .map(ActiveGroupInfo::of)
                .toList())
            .pendingGroups(serviceResponse.pendingGroups().stream()
                .map(PendingGroupInfo::of)
                .toList())
            .deniedGroups(serviceResponse.deniedGroups().stream()
                .map(DeniedGroupInfo::of)
                .toList())
            .build();
    }

    @Builder
    record ActiveGroupInfo(
        String id,
        String name,
        MemberInfo manager,
        int activeMemberCount
    ) {
        public static ActiveGroupInfo of(FindGroupSelfServiceResponse.ActiveGroupInfo item) {
            return ActiveGroupInfo.builder()
                .id(item.id())
                .name(item.name())
                .manager(MemberInfo.of(item.manager()))
                .activeMemberCount(item.activeMemberCount())
                .build();
        }
    }

    @Builder
    record PendingGroupInfo(
        String id,
        String name,
        int activeMemberCount
    ) {
        public static PendingGroupInfo of(FindGroupSelfServiceResponse.PendingGroupInfo item) {
            return PendingGroupInfo.builder()
                .id(item.id())
                .name(item.name())
                .activeMemberCount(item.activeMemberCount())
                .build();
        }
    }

    @Builder
    record DeniedGroupInfo(
        String id,
        String name,
        String deniedCause
    ) {
        public static DeniedGroupInfo of(FindGroupSelfServiceResponse.DeniedGroupInfo item) {
            return DeniedGroupInfo.builder()
                .id(item.id())
                .name(item.name())
                .deniedCause(item.deniedCause())
                .build();
        }
    }

    @Builder
    record MemberInfo(
        String nickname,
        String email
    ) {
        public static MemberInfo of(FindGroupSelfServiceResponse.MemberInfo item) {
            return MemberInfo.builder()
                .nickname(item.nickname())
                .email(item.email())
                .build();
        }
    }
}
