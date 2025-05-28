package com.divjazz.recommendic.cache.service.impl;

import com.divjazz.recommendic.cache.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheServiceImpl<K,V> implements CacheService<K,V> {

    private final RedisTemplate<K,V> redisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 20, TimeUnit.MINUTES);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long getRemainingTime(K key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    @Override
    public void evict(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(K key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
