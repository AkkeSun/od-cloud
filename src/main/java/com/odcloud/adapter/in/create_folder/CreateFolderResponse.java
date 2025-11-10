package com.odcloud.adapter.in.create_folder;

import com.odcloud.application.service.create_folder.CreateFolderServiceResponse;
import com.odcloud.infrastructure.util.ToStringUtil;
import lombok.Builder;

@Builder
public record CreateFolderResponse(
    Boolean status
) {

    public static CreateFolderResponse of(CreateFolderServiceResponse response) {
        return CreateFolderResponse.builder()
            .status(response.status())
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
