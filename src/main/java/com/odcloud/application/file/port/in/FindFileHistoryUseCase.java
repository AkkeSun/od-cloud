package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.find_file_history.FindFileHistoryCommand;
import com.odcloud.application.file.service.find_file_history.FindFileHistoryResponse;

public interface FindFileHistoryUseCase {

    FindFileHistoryResponse findHistory(FindFileHistoryCommand command);
}
