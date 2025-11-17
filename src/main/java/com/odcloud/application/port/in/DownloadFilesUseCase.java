package com.odcloud.application.port.in;

import com.odcloud.application.service.download_files.DownloadFilesServiceResponse;
import java.util.List;

public interface DownloadFilesUseCase {

    DownloadFilesServiceResponse download(List<Long> fileIds);
}
