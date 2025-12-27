package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Notice;
import com.odcloud.domain.model.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record PushFcmCommand(
    List<AccountDevice> devices,
    String title,
    String body,
    Map<String, String> data
) {

    public static PushFcmCommand ofNewSchedule(List<AccountDevice> devices, Group group,
        LocalDateTime startDt) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "schedule");
        data.put("groupId", group.getId());
        data.put("regDt", startDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return PushFcmCommand.builder()
            .devices(devices)
            .title(group.getName())
            .body(startDt.format(DateTimeFormatter.ofPattern("M월 d일 새로운 일정이 등록되었습니다.")))
            .data(data)
            .build();
    }

    public static PushFcmCommand ofNotificationSchedule(Schedule schedule,
        List<AccountDevice> devices, String title) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "schedule");
        data.put("groupId", schedule.isGroupSchedule() ? schedule.getGroupId() : "PRIVATE");
        data.put("regDt", schedule.getStartDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        boolean isToday = schedule.getStartDt().toLocalDate().equals(LocalDate.now());
        boolean hasMinute = schedule.getStartDt().getMinute() != 0;

        String timeFormat;
        if (isToday) {
            timeFormat = hasMinute ? "H시 m분" : "H시";
        } else {
            timeFormat = hasMinute ? "M월 d일 H시 m분" : "M월 d일 H시";
        }

        String body = String.format("%s %s",
            schedule.getStartDt().format(DateTimeFormatter.ofPattern(timeFormat)),
            schedule.getContent());

        return PushFcmCommand.builder()
            .devices(devices)
            .title(title)
            .body(body)
            .data(data)
            .build();
    }

    public static PushFcmCommand ofGroupPending(Group group, List<AccountDevice> devices) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "group-join");
        return PushFcmCommand.builder()
            .devices(devices)
            .title(group.getName())
            .body("가입 신청서가 등록되었습니다")
            .data(data)
            .build();
    }

    public static PushFcmCommand ofUpdateGroupStatus(List<AccountDevice> devices,
        UpdateGroupAccountStatusCommand command, Group group) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "group");

        return PushFcmCommand.builder()
            .devices(devices)
            .title(group.getName())
            .body(command.status().equals("ACTIVE") ?
                "가입 요청이 승인 되었습니다" : "가입 요청이 반려 되었습니다")
            .data(data)
            .build();
    }

    public static PushFcmCommand ofNotice(List<AccountDevice> devices, Group group,
        Notice notice) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "notice");
        data.put("groupId", group.getId());
        data.put("noticeId", String.valueOf(notice.getId()));

        return PushFcmCommand.builder()
            .devices(devices)
            .title(group.getName())
            .body("새로운 공지사항이 등록되었어요. 지금 바로 확인해보세요!")
            .data(data)
            .build();
    }
}
