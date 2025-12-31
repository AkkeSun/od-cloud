package com.odcloud.application.file.service.register_folder;

public record RegisterFolderServiceResponse(
    Boolean result
) {

    public static RegisterFolderServiceResponse ofSuccess() {
        return new RegisterFolderServiceResponse(Boolean.TRUE);
    }
}
