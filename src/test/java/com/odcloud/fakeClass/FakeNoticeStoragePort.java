package com.odcloud.fakeClass;

import com.odcloud.application.port.out.NoticeStoragePort;
import com.odcloud.domain.model.Notice;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FakeNoticeStoragePort implements NoticeStoragePort {

    public List<Notice> database = new ArrayList<>();
    public Long id = 0L;

    @Override
    public Notice save(Notice notice) {
        Notice savedNotice = Notice.builder()
            .id(notice.getId() == null ? ++id : notice.getId())
            .groupId(notice.getGroupId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .regDt(notice.getRegDt())
            .build();

        database.removeIf(n -> n.getId() != null && n.getId().equals(savedNotice.getId()));
        database.add(savedNotice);

        return savedNotice;
    }

    @Override
    public List<Notice> findByGroupId(String groupId, int limit) {
        return database.stream()
            .filter(notice -> notice.getGroupId().equals(groupId))
            .sorted(Comparator.comparing(Notice::getRegDt).reversed())
            .limit(limit)
            .toList();
    }
}
