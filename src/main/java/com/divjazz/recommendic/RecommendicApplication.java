package com.divjazz.recommendic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableCaching
@EnableJpaAuditing
public class RecommendicApplication {
    public final Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

    public RecommendicApplication() {
    }

    static void main(String[] args) {
        SpringApplication.run(RecommendicApplication.class, args);
    }


}