package com.odcloud.application.service.register_folder;

public record RegisterFolderServiceResponse(
    Boolean result
) {

    public static RegisterFolderServiceResponse ofSuccess() {
        return new RegisterFolderServiceResponse(Boolean.TRUE);
    }
}
