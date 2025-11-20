package com.odcloud.application.service.delete_folder;

public record DeleteFolderServiceResponse(
    Boolean result
) {

    public static DeleteFolderServiceResponse ofSuccess() {
        return new DeleteFolderServiceResponse(Boolean.TRUE);
    }
}
