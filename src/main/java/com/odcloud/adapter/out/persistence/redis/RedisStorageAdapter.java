package com.odcloud.adapter.out.persistence.redis;

import com.odcloud.application.port.out.RedisStoragePort;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RedisStorageAdapter implements RedisStoragePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void register(String key, String data, long ttl) {
        redisTemplate.opsForValue().set(key, data, ttl, TimeUnit.MILLISECONDS);
    }
}
