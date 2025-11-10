package com.odcloud.application.service.create_folder;

import com.odcloud.infrastructure.util.ToStringUtil;
import lombok.Builder;

@Builder
public record CreateFolderServiceResponse(
    Boolean status
) {

    public static CreateFolderServiceResponse ofSuccess() {
        return CreateFolderServiceResponse.builder()
            .status(true)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
