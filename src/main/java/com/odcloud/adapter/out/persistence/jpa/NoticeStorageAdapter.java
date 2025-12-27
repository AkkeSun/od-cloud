package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.NoticeStoragePort;
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
    public List<Notice> findByGroupId(String groupId, int limit) {
        return repository.findByGroupId(groupId, limit).stream()
            .map(NoticeEntity::toDomain)
            .toList();
    }
}
