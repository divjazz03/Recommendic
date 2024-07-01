package com.divjazz.recommendic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class RecommendicApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendicApplication.class, args);
	}

}
