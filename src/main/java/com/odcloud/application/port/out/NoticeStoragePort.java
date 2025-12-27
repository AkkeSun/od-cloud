package com.odcloud.application.port.out;

import com.odcloud.domain.model.Notice;

public interface NoticeStoragePort {

    Notice save(Notice notice);
}
