package com.odcloud.application.group.service.find_group;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.domain.model.Notice;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupServiceResponse(
    Long id,
    String name,
    MemberInfo manager,
    List<MemberInfo> members,
    int activeMemberCount,
    Long storageUsed,
    Long storageTotal,
    List<NoticeInfo> notices
) {

    public static FindGroupServiceResponse of(Group group, List<Notice> notices) {
        MemberInfo managerInfo = group.getGroupMembers().stream()
            .filter(member -> member.getEmail().equals(group.getOwnerEmail()))
            .findFirst()
            .map(MemberInfo::of)
            .orElse(null);

        List<MemberInfo> memberInfos = group.getGroupMembers().stream()
            .filter(member -> "ACTIVE".equals(member.getStatus()))
            .filter(member -> !member.getEmail().equals(group.getOwnerEmail()))
            .map(MemberInfo::of)
            .toList();

        int activeCount = (int) group.getGroupMembers().stream()
            .filter(member -> "ACTIVE".equals(member.getStatus()))
            .count();

        List<NoticeInfo> noticeInfos = notices.stream()
            .map(NoticeInfo::of)
            .toList();

        return FindGroupServiceResponse.builder()
            .id(group.getId())
            .name(group.getName())
            .manager(managerInfo)
            .members(memberInfos)
            .activeMemberCount(activeCount)
            .storageUsed(group.getStorageUsed())
            .storageTotal(group.getStorageTotal())
            .notices(noticeInfos)
            .build();
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

    @Builder
    public record NoticeInfo(
        Long id,
        String title,
        String content
    ) {

        public static NoticeInfo of(Notice notice) {
            return NoticeInfo.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
        }
    }
}
