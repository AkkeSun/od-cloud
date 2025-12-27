package com.odcloud.application.port.out;

import com.odcloud.domain.model.Notice;
import java.util.List;

public interface NoticeStoragePort {

    Notice save(Notice notice);

    List<Notice> findByGroupId(String groupId, int limit);
}
