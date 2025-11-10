package com.odcloud.application.service.update_group_account_status;

import com.odcloud.infrastructure.util.ToStringUtil;
import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusServiceResponse(
    Boolean status
) {

    public static UpdateGroupAccountStatusServiceResponse ofSuccess() {
        return UpdateGroupAccountStatusServiceResponse.builder()
            .status(true)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
