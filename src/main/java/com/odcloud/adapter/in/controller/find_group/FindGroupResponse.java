package com.odcloud.adapter.in.controller.find_group;

import com.odcloud.application.service.find_group.FindGroupServiceResponse;
import java.util.List;
import lombok.Builder;

@Builder
record FindGroupResponse(
    String id,
    String name,
    MemberInfo manager,
    List<MemberInfo> members,
    int activeMemberCount,
    List<NoticeInfo> notices
) {

    public static FindGroupResponse of(FindGroupServiceResponse serviceResponse) {
        return FindGroupResponse.builder()
            .id(serviceResponse.id())
            .name(serviceResponse.name())
            .manager(MemberInfo.of(serviceResponse.manager()))
            .members(serviceResponse.members().stream()
                .map(MemberInfo::of)
                .toList())
            .activeMemberCount(serviceResponse.activeMemberCount())
            .notices(serviceResponse.notices().stream()
                .map(NoticeInfo::of)
                .toList())
            .build();
    }

    @Builder
    record MemberInfo(
        String nickname,
        String email
    ) {

        public static MemberInfo of(FindGroupServiceResponse.MemberInfo item) {
            return MemberInfo.builder()
                .nickname(item.nickname())
                .email(item.email())
                .build();
        }
    }

    @Builder
    record NoticeInfo(
        Long id,
        String title,
        String content
    ) {

        public static NoticeInfo of(FindGroupServiceResponse.NoticeInfo item) {
            return NoticeInfo.builder()
                .id(item.id())
                .title(item.title())
                .content(item.content())
                .build();
        }
    }
}
