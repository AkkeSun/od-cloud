package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_files.DownloadFilesResponse;
import java.util.List;

public interface DownloadFilesUseCase {

    DownloadFilesResponse download(List<Long> fileIds);
}
