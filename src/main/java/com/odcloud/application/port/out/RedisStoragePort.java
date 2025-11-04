package com.odcloud.application.port.out;

public interface RedisStoragePort {

    void register(String key, String data, long ttl);

    <T> T findData(String key, Class<T> clazz);
}
