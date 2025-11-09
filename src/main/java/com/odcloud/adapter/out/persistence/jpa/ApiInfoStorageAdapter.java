package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.ApiInfoStoragePort;
import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.domain.model.ApiInfo;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

@Component
@Transactional
class ApiInfoStorageAdapter implements ApiInfoStoragePort {

    private List<ApiInfo> apiInfoCache;
    private final AntPathMatcher matcher;
    private final ApiInfoRepository apiInfoRepository;

    ApiInfoStorageAdapter(ApiInfoRepository apiInfoRepository) {
        this.apiInfoRepository = apiInfoRepository;
        this.matcher = new AntPathMatcher();
        this.apiInfoCache = new ArrayList<>();
    }

    @Override
    public List<ApiInfo> findAll() {
        if (apiInfoCache.isEmpty()) {
            apiInfoCache = apiInfoRepository.findAll().stream()
                .sorted(Comparator.comparingInt((ApiInfo domain) -> {
                    return StringUtils.countOccurrencesOf(domain.uriPattern(), "{");
                }))
                .collect(Collectors.toList());
        }
        return apiInfoCache;
    }

    @Override
    public ApiInfo findByApiCallLog(ApiCallLog apiCallLog) {
        return findAll().stream()
            .filter(api -> Objects.equals(api.httpMethod(), apiCallLog.getHttpMethod()))
            .filter(api -> matcher.match(api.uriPattern(), apiCallLog.getUri()))
            .findFirst().orElse(null);
    }

    @Override
    public void deleteApiInfoCache() {
        apiInfoCache.clear();
    }
}
