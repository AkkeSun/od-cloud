package com.odcloud.adapter.in.controller.group.find_groups;

import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindGroupsRequest {

    @NotBlank(message = "keyword는 필수입니다")
    private String keyword;

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
