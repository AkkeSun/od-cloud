package com.odcloud.fakeClass;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
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

        return null;
    }

    @Override
    public <T> List<T> findDataList(String key, Class<T> clazz) {
        return List.of();
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> task) {
        return task.get();
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> task, long waitTimeMs,
        long leaseTimeMs) {
        return task.get();
    }

    public void reset() {
        database.clear();
        shouldThrowException = false;
    }
}
