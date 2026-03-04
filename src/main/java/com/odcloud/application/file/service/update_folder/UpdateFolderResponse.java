package com.odcloud.application.file.service.update_folder;

import lombok.Builder;

@Builder
public record UpdateFolderResponse(Boolean result) {

    public static UpdateFolderResponse ofSuccess() {
        return new UpdateFolderResponse(Boolean.TRUE);
    }
}
