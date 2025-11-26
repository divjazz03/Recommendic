package com.divjazz.recommendic;

import com.divjazz.recommendic.config.CustomPostgresContainer;
import com.divjazz.recommendic.config.CustomRedisContainer;
import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@Slf4j
public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    static final PostgreSQLContainer<?> postgresContainer = CustomPostgresContainer.getInstance();
    static final RedisContainer redisContainer = CustomRedisContainer.getInstance()
            .withExposedPorts(6379);

    static {
        Startables.deepStart(postgresContainer, redisContainer).join();
    }
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Integer redisPort = redisContainer.getMappedPort(6379);
        String redisHost = redisContainer.getHost();

        log.info("Redis running at {}:{}", redisHost, redisPort);

        TestPropertyValues.of(
                "spring.datasource.url=%s".formatted(postgresContainer.getJdbcUrl()),
                "spring.datasource.username=%s".formatted(postgresContainer.getUsername()),
                "spring.datasource.password=%s".formatted(postgresContainer.getPassword()),
                "spring.data.redis.host=%s".formatted(redisContainer.getHost()),
                "spring.data.redis.port=%s".formatted(redisContainer.getMappedPort(6379))

        ).applyTo(applicationContext.getEnvironment());
    }
}
