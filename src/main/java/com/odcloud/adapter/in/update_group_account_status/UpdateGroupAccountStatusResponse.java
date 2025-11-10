package com.odcloud.adapter.in.update_group_account_status;

import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
import com.odcloud.infrastructure.util.ToStringUtil;
import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusResponse(
    String message
) {

    public static UpdateGroupAccountStatusResponse of(
        UpdateGroupAccountStatusServiceResponse response) {
        return UpdateGroupAccountStatusResponse.builder()
            .message(response.message())
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
