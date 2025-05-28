package com.divjazz.recommendic.cache.service;

import java.util.concurrent.TimeUnit;

public interface CacheService<K,V> {

    void put(K key, V value);
    V get(K key);

    Long getRemainingTime(K key, TimeUnit timeUnit);
    void evict(K key);

    boolean hasKey(K key);
}
