package com.divjazz.recommendic;

import com.divjazz.recommendic.user.domain.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;


@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class RecommendicApplication implements CommandLineRunner {
	Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RecommendicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RequestContext.setUserId(0);
		log.info("Current Context Id {} ", RequestContext.getUserId());

	}
}
