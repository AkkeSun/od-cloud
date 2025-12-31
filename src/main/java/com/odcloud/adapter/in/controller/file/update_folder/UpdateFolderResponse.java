package com.odcloud.adapter.in.controller.file.update_folder;

import com.odcloud.application.file.service.update_folder.UpdateFolderServiceResponse;
import lombok.Builder;

@Builder
public record UpdateFolderResponse(Boolean result) {

    public static UpdateFolderResponse of(UpdateFolderServiceResponse response) {
        return UpdateFolderResponse.builder()
            .result(response.result())
            .build();
    }
}
