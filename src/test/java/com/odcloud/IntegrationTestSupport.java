package com.odcloud;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import redis.embedded.RedisServer;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = IntegrationTestSupport.EmbeddedRedisInitializer.class)
public abstract class IntegrationTestSupport {

    public static class EmbeddedRedisInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static RedisServer redisServer;

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            if (redisServer == null) {
                int port = Integer.parseInt(
                    applicationContext.getEnvironment()
                        .getProperty("spring.data.redis.port", "6377")
                );
                try {
                    redisServer = new RedisServer(port);
                    redisServer.start();
                } catch (Exception e) {
                    throw new RuntimeException("embedded Redis 시작 실패", e);
                }
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (redisServer != null && redisServer.isActive()) {
                        try {
                            redisServer.stop();
                        } catch (Exception ignored) {
                        }
                    }
                }));
            }
        }
    }
}