package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Group;
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

}
