package com.odcloud.application.port.in.command;

import java.util.List;
import lombok.Builder;

@Builder
public record DownloadFileCommand(List<Long> fileIds) {

}
