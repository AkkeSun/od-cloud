package com.odcloud.adapter.in.controller.delete_file;

import lombok.Builder;

@Builder
record DeleteFileResponseItem(
    Long fileId,
    String errorMessage
) {
}
