package com.divjazz.recommendic.config;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomRedisContainer extends RedisContainer {

    private static final String IMAGE_VERSION = "redis:8.0-M03-alpine";
    private static CustomRedisContainer redisContainer;

    private CustomRedisContainer () {
        super(DockerImageName.parse(IMAGE_VERSION));
    }

    public static CustomRedisContainer getInstance() {
        if (redisContainer == null) {
            redisContainer = new CustomRedisContainer();
        }
        return redisContainer;
    }
}
