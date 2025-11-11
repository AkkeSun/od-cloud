package com.odcloud.adapter.in.register_folder;

import com.odcloud.application.service.register_folder.RegisterFolderServiceResponse;

public record RegisterFolderResponse(
    Boolean result
) {

    public static RegisterFolderResponse of(RegisterFolderServiceResponse response) {
        return new RegisterFolderResponse(response.result());
    }
}
