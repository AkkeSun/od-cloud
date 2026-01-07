package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.domain.model.Notice;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NoticeStorageAdapter implements NoticeStoragePort {

    private final NoticeRepository repository;

    @Override
    public Notice save(Notice notice) {
        NoticeEntity entity = repository.save(NoticeEntity.of(notice));
        return entity.toDomain();
    }

    @Override
    public Notice findById(Long noticeId) {
        return repository.findById(noticeId)
            .map(NoticeEntity::toDomain)
            .orElseThrow(() -> new com.odcloud.infrastructure.exception.CustomBusinessException(
                com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_NOTICE));
    }

    @Override
    public List<Notice> findByGroupId(Long groupId, int limit) {
        return repository.findByGroupId(groupId, limit).stream()
            .map(NoticeEntity::toDomain)
            .toList();
    }

    @Override
    public void delete(Notice notice) {
        repository.delete(notice.getId());
    }

    @Override
    public void update(Notice notice) {
        repository.update(notice.getId(), notice.getTitle(), notice.getContent(),
            notice.getModDt());
    }
}
