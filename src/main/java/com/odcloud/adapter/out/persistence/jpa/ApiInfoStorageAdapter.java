package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.ApiInfoStoragePort;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.domain.model.ApiInfo;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
class ApiInfoStorageAdapter implements ApiInfoStoragePort {

    private static final String API_INFO_CACHE_KEY = "api:info:all";

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ApiInfoRepository apiInfoRepository;
    private final RedisStoragePort redisStoragePort;

    @Override
    public List<ApiInfo> findAll() {
        // 순수 DB 조회 (캐시는 Filter 레벨에서 처리)
        return apiInfoRepository.findAll().stream()
            .sorted(Comparator.comparingInt((ApiInfo domain) -> {
                return StringUtils.countOccurrencesOf(domain.uriPattern(), "{");
            }))
            .collect(Collectors.toList());
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
        redisStoragePort.delete(API_INFO_CACHE_KEY);
        log.debug("ApiInfo 캐시 삭제 완료");
    }
}
