package com.odcloud.fakeClass;

import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.domain.model.Notice;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
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
            .writerEmail(notice.getWriterEmail())
            .regDt(notice.getRegDt())
            .build();

        database.removeIf(n -> n.getId() != null && n.getId().equals(savedNotice.getId()));
        database.add(savedNotice);

        return savedNotice;
    }

    @Override
    public Notice findById(Long noticeId) {
        return database.stream()
            .filter(notice -> notice.getId() != null && notice.getId().equals(noticeId))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_NOTICE));
    }

    @Override
    public List<Notice> findByGroupId(Long groupId, int limit) {
        return database.stream()
            .filter(notice -> notice.getGroupId().equals(groupId))
            .sorted(Comparator.comparing(Notice::getRegDt).reversed())
            .limit(limit)
            .toList();
    }

    @Override
    public void delete(Notice notice) {
        database.removeIf(n -> n.getId() != null && n.getId().equals(notice.getId())
            && n.getGroupId().equals(notice.getGroupId()));
    }

    @Override
    public void update(Notice notice) {
        Notice existingNotice = findById(notice.getId());

        Notice updatedNotice = Notice.builder()
            .id(existingNotice.getId())
            .groupId(existingNotice.getGroupId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .writerEmail(existingNotice.getWriterEmail())
            .regDt(existingNotice.getRegDt())
            .modDt(notice.getModDt())
            .build();

        database.removeIf(n -> n.getId() != null && n.getId().equals(notice.getId()));
        database.add(updatedNotice);
    }
}
