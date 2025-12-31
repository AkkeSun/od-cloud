package com.odcloud.adapter.out.persistence.redis;

import static com.odcloud.infrastructure.util.JsonUtil.parseJson;
import static com.odcloud.infrastructure.util.JsonUtil.parseJsonList;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
class RedisStorageAdapter implements RedisStoragePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void register(String key, String data) {
        redisTemplate.opsForValue().set(key, data);
    }

    @Override
    public void register(String key, String data, long ttl) {
        redisTemplate.opsForValue().set(key, data, ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> T findData(String key, Class<T> clazz) {
        String redisData = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(redisData)) {
            return null;
        }
        if (clazz == String.class) {
            return clazz.cast(redisData);
        }

        return parseJson(redisData, clazz);
    }

    @Override
    public <T> List<T> findDataList(String key, Class<T> clazz) {
        String redisData = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(redisData)) {
            return Collections.emptyList();
        }
        return parseJsonList(redisData, clazz);
    }
}
