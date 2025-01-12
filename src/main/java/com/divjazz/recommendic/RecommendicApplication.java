package com.divjazz.recommendic;

import com.divjazz.recommendic.user.domain.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
}