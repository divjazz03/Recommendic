package com.divjazz.recommendic.global.cache;

import com.divjazz.recommendic.global.cache.service.CacheService;
import com.divjazz.recommendic.global.cache.service.impl.InMemoryCacheServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(RedisConfig.class)
public class InMemoryCacheConfig {
    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }


    @Bean("inMemoryLoginHandlerBean")
    CacheService<Object, Object> cacheService () {
        return new InMemoryCacheServiceImpl<>();
    }
}
