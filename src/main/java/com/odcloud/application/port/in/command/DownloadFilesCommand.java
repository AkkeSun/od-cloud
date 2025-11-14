package com.odcloud.application.port.in.command;

import java.util.List;
import lombok.Builder;

@Builder
public record DownloadFilesCommand(List<Long> fileIds) {

}
