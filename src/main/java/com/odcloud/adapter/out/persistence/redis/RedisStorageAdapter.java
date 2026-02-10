package com.odcloud.adapter.out.persistence.redis;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_LOCK_ACQUISITION_FAILED;
import static com.odcloud.infrastructure.util.JsonUtil.parseJson;
import static com.odcloud.infrastructure.util.JsonUtil.parseJsonList;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class RedisStorageAdapter implements RedisStoragePort {

    private final long DEFAULT_WAIT_TIME = 7;
    private final long DEFAULT_LEASE_TIME = 5;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final TransactionTemplate transactionTemplate;

    RedisStorageAdapter(RedissonClient redissonClient,
        RedisTemplate<String, String> redisTemplate,
        PlatformTransactionManager transactionManager) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(
            TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

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

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> task) {
        return executeWithLock(lockKey, task, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> task, long waitTimeMs,
        long leaseTimeMs) {
        RLock lock = redissonClient.getLock("lock:" + lockKey);

        try {
            boolean acquired = lock.tryLock(waitTimeMs, leaseTimeMs, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Failed to acquire lock for key: {}", lockKey);
                throw new CustomBusinessException(Business_LOCK_ACQUISITION_FAILED);
            } else {
                log.info("Successfully acquired lock for key: {}", lockKey);
            }

            return transactionTemplate.execute(status -> task.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for lock: {}", lockKey, e);
            throw new CustomBusinessException(Business_LOCK_ACQUISITION_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
