package com.odcloud.adapter.in.delete_folder;

import com.odcloud.application.service.delete_folder.DeleteFolderServiceResponse;
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
