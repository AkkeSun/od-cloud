package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_files.DownloadFilesServiceResponse;
import java.util.List;

public interface DownloadFilesUseCase {

    DownloadFilesServiceResponse download(List<Long> fileIds);
}
