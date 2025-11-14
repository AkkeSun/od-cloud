package com.odcloud.application.service.download_file;

public record DownloadFileServiceResponse(
    String fileName,
    byte[] content,
    String contentType
) {

    public static DownloadFileServiceResponse of(String fileName, byte[] content,
        String contentType) {
        return new DownloadFileServiceResponse(fileName, content, contentType);
    }
}
