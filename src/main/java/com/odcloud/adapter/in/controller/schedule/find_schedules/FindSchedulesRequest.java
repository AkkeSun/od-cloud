package com.odcloud.adapter.in.controller.schedule.find_schedules;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.DateUtil;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.DatePattern;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FindSchedulesRequest {

    @DatePattern(message = "기준일은 yyyy-MM-dd 형식만 입력 가능합니다", groups = CustomGroups.class)
    private String baseDate;

    private String filterType;

    FindSchedulesCommand toCommand(Account account) {
        return FindSchedulesCommand.builder()
            .account(account)
            .baseDate(baseDate == null ? LocalDate.now() : DateUtil.parseDate(baseDate))
            .filterType(filterType)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
