package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record DownloadFolderCommand(Long folderId) {

}
