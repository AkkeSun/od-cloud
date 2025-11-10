package com.odcloud.application.service.download_file;

import lombok.Builder;

@Builder
public record DownloadFileServiceResponse(byte[] fileData, String fileName, String contentType) {

}
