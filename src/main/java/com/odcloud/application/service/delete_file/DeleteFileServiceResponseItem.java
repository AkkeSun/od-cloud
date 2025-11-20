package com.odcloud.application.service.delete_file;

import lombok.Builder;

@Builder
public record DeleteFileServiceResponseItem(
    Long fileId,
    String errorMessage
) {

    public static DeleteFileServiceResponseItem ofSuccess(Long fileId) {
        return DeleteFileServiceResponseItem.builder()
            .fileId(fileId)
            .errorMessage(null)
            .build();
    }
}
