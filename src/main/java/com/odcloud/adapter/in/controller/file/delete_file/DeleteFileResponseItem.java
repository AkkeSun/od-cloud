package com.odcloud.adapter.in.controller.file.delete_file;

import lombok.Builder;

@Builder
record DeleteFileResponseItem(
    Long fileId,
    String errorMessage
) {
}
