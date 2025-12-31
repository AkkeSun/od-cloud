package com.odcloud.application.auth.port.out;

import java.util.List;

public interface RedisStoragePort {

    void register(String key, String data);

    void register(String key, String data, long ttl);

    <T> T findData(String key, Class<T> clazz);

    <T> List<T> findDataList(String key, Class<T> clazz);
}
