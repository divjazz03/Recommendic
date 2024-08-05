package com.divjazz.recommendic.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheStore<String, Integer> loginCache() {
        return new CacheStore<>(50, TimeUnit.MINUTES);
    }
}
