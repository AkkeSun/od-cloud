package com.odcloud.application.file.service.register_folder;

public record RegisterFolderResponse(
    Boolean result
) {

    public static RegisterFolderResponse ofSuccess() {
        return new RegisterFolderResponse(Boolean.TRUE);
    }
}
