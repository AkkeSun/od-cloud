package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.NoticeStoragePort;
import com.odcloud.domain.model.Notice;
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
}
