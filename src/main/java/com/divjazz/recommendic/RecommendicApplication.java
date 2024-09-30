package com.divjazz.recommendic;

import com.divjazz.recommendic.externalApi.openFDA.OpenFDAQuery;
import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Set;


@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class RecommendicApplication implements CommandLineRunner {
	public final Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

	private final OpenFDAQuery openFDAQuery;
	public RecommendicApplication(OpenFDAQuery openFDAQuery) {
		this.openFDAQuery = openFDAQuery;
	}

	public static void main(String[] args) {
		SpringApplication.run(RecommendicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RequestContext.setUserId(0);
		var output = openFDAQuery.queryDrugs(List.of("products.marketing_status:\"Discontinued\""));
		log.info("result = {}" , output);
	}
}