package com.odcloud.application.auth.port.out;

import java.util.List;
import java.util.function.Supplier;

public interface RedisStoragePort {

    void register(String key, String data);

    void register(String key, String data, long ttl);

    <T> T findData(String key, Class<T> clazz);

    <T> List<T> findDataList(String key, Class<T> clazz);

    <T> T executeWithLock(String lockKey, Supplier<T> task);

    <T> T executeWithLock(String lockKey, Supplier<T> task, long waitTimeMs, long leaseTimeMs);
}
