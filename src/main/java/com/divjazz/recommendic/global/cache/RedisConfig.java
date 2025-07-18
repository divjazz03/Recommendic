package com.divjazz.recommendic.global.cache;

import com.divjazz.recommendic.global.cache.service.CacheService;
import com.divjazz.recommendic.global.cache.service.impl.RedisCacheServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean("loginHandlerRedisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    @Primary
    public RedisCacheConfiguration generalCacheConfiguration(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("recommendic::")
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .disableCachingNullValues();
    }

    @Bean("loginHandlerBean")
    @Primary
    public CacheService<Object, Object> cacheService(@Qualifier("loginHandlerRedisTemplate") RedisTemplate<Object, Object> redisTemplate) {
        return new RedisCacheServiceImpl<>(redisTemplate);
    }
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(ObjectMapper objectMapper){
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
