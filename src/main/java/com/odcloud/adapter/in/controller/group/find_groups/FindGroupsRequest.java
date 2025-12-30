package com.odcloud.adapter.in.controller.group.find_groups;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindGroupsRequest {

    @NotBlank(message = "keyword는 필수입니다")
    private String keyword;

    public FindGroupsCommand toCommand() {
        return FindGroupsCommand.builder()
            .keyword(keyword)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
