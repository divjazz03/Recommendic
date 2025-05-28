package com.divjazz.recommendic.cache.service.impl;

import com.divjazz.recommendic.cache.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class InMemoryCacheServiceImpl<K,V> implements CacheService<K,V> {
    private final Cache<K, TimedValue<V>> cache;
    private static final long TTLValue = 20;
    public static final TimeUnit TTLTimeUnit = TimeUnit.MINUTES;


    public InMemoryCacheServiceImpl() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(TTLValue, TTLTimeUnit)
                .maximumSize(2000)
                .build();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new TimedValue<>(value, Instant.now().plus(TTLValue,TTLTimeUnit.toChronoUnit())));
    }

    @Override
    public V get(K key) {
        var value = cache.getIfPresent(key);
        return value != null ? value.value(): null;
    }

    @Override
    public Long getRemainingTime(K key, TimeUnit timeUnit) {

        var value = cache.getIfPresent(key);
        if (value == null) {
            return 0L;
        }

        var expiry = value.expiry();

        var duration = Duration
                .of(expiry.getEpochSecond() - Instant.now().getEpochSecond(),
                        timeUnit.toChronoUnit());
        return switch (timeUnit) {
            case DAYS -> duration.toDays();
            case HOURS -> duration.toHours();
            case MINUTES -> duration.toMinutes();
            case SECONDS -> duration.toSeconds();
            case NANOSECONDS -> duration.toNanos();
            case MILLISECONDS -> duration.toMillis();
            default -> duration.getSeconds();
        };
    }

    @Override
    public void evict(K key) {
        cache.invalidate(key);
    }

    @Override
    public boolean hasKey(K key) {
        return cache.getIfPresent(key) != null;
    }

    record TimedValue<V>(V value, Instant expiry) {}

}
