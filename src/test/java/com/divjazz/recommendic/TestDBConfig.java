package com.divjazz.recommendic;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.io.IOException;

@Profile("test")
@TestConfiguration
public class TestDBConfig {

    @Bean
    @Primary
    public DataSource testDataSource() throws IOException {
        EmbeddedPostgres embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(0)
                .start();
            return embeddedPostgres.getPostgresDatabase();

    }
}
