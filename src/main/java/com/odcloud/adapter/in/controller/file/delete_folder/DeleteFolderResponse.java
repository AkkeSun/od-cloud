package com.odcloud.adapter.in.controller.file.delete_folder;

import com.odcloud.application.file.service.delete_folder.DeleteFolderServiceResponse;
import lombok.Builder;

@Builder
record DeleteFolderResponse(
    Boolean result
) {

    static DeleteFolderResponse of(DeleteFolderServiceResponse serviceResponse) {
        return DeleteFolderResponse.builder()
            .result(serviceResponse.result())
            .build();
    }
}
