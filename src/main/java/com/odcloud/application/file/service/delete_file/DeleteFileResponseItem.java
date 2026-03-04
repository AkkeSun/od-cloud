package com.odcloud.application.file.service.delete_file;

import lombok.Builder;

@Builder
public record DeleteFileResponseItem(
    Long fileId,
    String errorMessage
) {

    public static DeleteFileResponseItem ofSuccess(Long fileId) {
        return DeleteFileResponseItem.builder()
            .fileId(fileId)
            .errorMessage(null)
            .build();
    }
}
