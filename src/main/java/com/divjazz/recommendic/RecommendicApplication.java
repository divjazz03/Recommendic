package com.divjazz.recommendic;

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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Set;


@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class RecommendicApplication implements CommandLineRunner {
	public final Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

	public final GeneralUserService userService;

	public RecommendicApplication(GeneralUserService userService) {
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(RecommendicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RequestContext.setUserId(0);
		log.info("Current Context Id {} ", RequestContext.getUserId());
		User user = userService.retrieveUserByUserId("f779d895-001b-4109-a02e-bd5b6ddd0535");
		Role role = user.getRole();
		Set<? extends GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(role.getPermissions()));
		var apiAuthentication = ApiAuthentication.authenticated(user, authorities);
		SecurityContextHolder.createEmptyContext().setAuthentication(apiAuthentication);
	}
}