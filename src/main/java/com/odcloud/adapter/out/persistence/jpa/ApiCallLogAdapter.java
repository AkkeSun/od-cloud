package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.ApiCallLogStoragePort;
import com.odcloud.domain.model.ApiCallLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
class ApiCallLogAdapter implements ApiCallLogStoragePort {

    private final ApiCallLogRepository repository;

    @Override
    public ApiCallLog register(ApiCallLog apiCallLog) {
        return repository.save(apiCallLog);
    }
}
