package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.util.JsonUtil.parseJson;
import static com.odcloud.infrastructure.util.JsonUtil.parseJsonList;

import com.odcloud.application.port.out.RedisStoragePort;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeRedisStoragePort implements RedisStoragePort {

    public Map<String, String> database = new HashMap<>();
    public boolean shouldThrowException = false;

    @Override
    public void register(String key, String data) {
        if (shouldThrowException) {
            throw new RuntimeException("Redis storage failure");
        }
        database.put(key, data);
        log.info("FakeRedisStoragePort register: key={}", key);
    }

    @Override
    public void register(String key, String data, long ttl) {
        if (shouldThrowException) {
            throw new RuntimeException("Redis storage failure");
        }
        database.put(key, data);
        log.info("FakeRedisStoragePort register with TTL: key={}, ttl={}", key, ttl);
    }

    @Override
    public <T> T findData(String key, Class<T> clazz) {
        String data = database.get(key);
        if (data == null) {
            return null;
        }

        if (clazz == String.class) {
            return clazz.cast(data);
        }

        try {
            return parseJson(data, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON for key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> List<T> findDataList(String key, Class<T> clazz) {
        String data = database.get(key);
        if (data == null) {
            return Collections.emptyList();
        }

        try {
            return parseJsonList(data, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON list for key: {}", key, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(String key) {
        if (shouldThrowException) {
            throw new RuntimeException("Redis delete failure");
        }
        database.remove(key);
        log.info("FakeRedisStoragePort delete: key={}", key);
    }

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
