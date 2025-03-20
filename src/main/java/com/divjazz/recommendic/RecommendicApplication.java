package com.divjazz.recommendic;

import com.divjazz.recommendic.user.domain.RequestContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.GeoModule;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class RecommendicApplication implements CommandLineRunner {
    public final Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

    public RecommendicApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(RecommendicApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RequestContext.setUserId(0);
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .configure(SerializationFeature.CLOSE_CLOSEABLE, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
    }
}