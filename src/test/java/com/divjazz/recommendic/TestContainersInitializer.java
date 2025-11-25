package com.divjazz.recommendic;

import com.divjazz.recommendic.config.CustomPostgresContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    static final PostgreSQLContainer<?> postgresContainer = CustomPostgresContainer.getInstance();

    static {
        Startables.deepStart(postgresContainer).join();
    }
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=%s".formatted(postgresContainer.getJdbcUrl()),
                "spring.datasource.username=%s".formatted(postgresContainer.getUsername()),
                "spring.datasource.password=%s".formatted(postgresContainer.getPassword())

        ).applyTo(applicationContext.getEnvironment());
    }
}
