package com.odcloud.application.file.service.update_folder;

import lombok.Builder;

@Builder
public record UpdateFolderServiceResponse(Boolean result) {

    public static UpdateFolderServiceResponse ofSuccess() {
        return new UpdateFolderServiceResponse(Boolean.TRUE);
    }
}
