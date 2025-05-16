package com.divjazz.recommendic.general;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component("customCacheKeyGenerator")
public class CustomCacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return method.getName() + "_" + Arrays.stream(params)
                .map(Object::toString)
                .reduce((a,b) -> a + "_" + b)
                .orElse("unknown");
    }
}
