package com.odcloud.application.file.service.delete_folder;

public record DeleteFolderResponse(
    Boolean result
) {

    public static DeleteFolderResponse ofSuccess() {
        return new DeleteFolderResponse(Boolean.TRUE);
    }
}
