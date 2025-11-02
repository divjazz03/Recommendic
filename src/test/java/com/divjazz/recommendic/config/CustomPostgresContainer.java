package com.divjazz.recommendic.config;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomPostgresContainer extends PostgreSQLContainer<CustomPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:18-alpine3.22";
    private static CustomPostgresContainer customPostgresContainer;

    private CustomPostgresContainer () {
        super(DockerImageName.parse(IMAGE_VERSION));
    }

    public static CustomPostgresContainer getInstance() {
        if (customPostgresContainer == null) {
            customPostgresContainer = new CustomPostgresContainer()
                    .withDatabaseName("testdb")
                    .withPassword("test")
                    .withUsername("test");
        }
        return customPostgresContainer;
    }
}
