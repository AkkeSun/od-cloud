package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record DownloadFileCommand(Long fileId) {

}
