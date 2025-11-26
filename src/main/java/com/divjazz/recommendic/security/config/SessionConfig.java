package com.divjazz.recommendic.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@EnableRedisHttpSession
@Configuration
@Profile({"dev, prod"})
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
}
