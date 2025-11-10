package com.odcloud.application.service.update_group_account_status;

import com.odcloud.infrastructure.util.ToStringUtil;
import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusServiceResponse(
    String message
) {

    public static UpdateGroupAccountStatusServiceResponse ofSuccess() {
        return UpdateGroupAccountStatusServiceResponse.builder()
            .message("상태 변경이 완료되었습니다.")
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
