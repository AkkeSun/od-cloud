package com.odcloud.infrastructure.config;

import com.odcloud.infrastructure.constant.ProfileConstant;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    private final ProfileConstant constant;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (constant.profile().equals("prod")) {
            config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
                .setPassword(redisPassword);
        } else {
            config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        }

        return Redisson.create(config);
    }
}
