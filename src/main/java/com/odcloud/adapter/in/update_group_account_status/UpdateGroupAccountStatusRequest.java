package com.odcloud.adapter.in.update_group_account_status;

import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateGroupAccountStatusRequest(
    @NotBlank(message = "상태는 필수값 입니다")
    String status
) {

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
