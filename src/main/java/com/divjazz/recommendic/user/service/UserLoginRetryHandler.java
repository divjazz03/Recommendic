package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.cache.service.CacheService;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class UserLoginRetryHandler {

    @Value("${auth.maxAttempts}")
    private int MAX_ATTEMPTS;
    @Value("${auth.lockoutDuration_minutes}")
    private int LOCKOUT_DURATION_MINUTES;

    private final CacheService<Object, Object> cacheService;

    public UserLoginRetryHandler(CacheService<Object, Object> cacheService) {
        this.cacheService = cacheService;
    }

    public void handleFailedAttempts(String email) {
        var failedAttempts = incrementFailedAttempt(email);
        var remainingAttempts = MAX_ATTEMPTS - Math.min(MAX_ATTEMPTS, failedAttempts);
        if (failedAttempts >= MAX_ATTEMPTS) {
            lockAccount(email);
            throw new LockedException(
                    "Your account has been locked due to too many failed attempts. Please try again in "
                            + getRemainingLockTime(email) + " minutes");
        }
        throw new LoginFailedException(
                "Invalid credentials: You have failed %s times, %s attempts left"
                        .formatted(failedAttempts,
                        remainingAttempts));
    }

    public void handleSuccessFulAttempt(String email) {
        resetFailedAttempts(email);
    }

    private void resetFailedAttempts(String email) {
        String attemptKey = getAttemptKey(email);
        cacheService.evict(attemptKey);
    }

    private int incrementFailedAttempt(String email) {
        String attemptKey = getAttemptKey(email);
        Long currentAttempts = (Long) cacheService.get(attemptKey);
        if (currentAttempts == null) {
            currentAttempts = 1L;
            cacheService.put(attemptKey, currentAttempts);
            return currentAttempts.intValue();
        }
        cacheService.put(attemptKey,++currentAttempts);
        return currentAttempts.intValue();
    }

    private int getFailedAttempts(String email) {
        String attemptKey = getAttemptKey(email);
        Long attempts = (Long) cacheService.get(attemptKey);
        return attempts != null ? attempts.intValue() : 0;
    }

    private long getRemainingLockTime(String email) {
        String lockKey = getLockKey(email);
        Long expiry = cacheService.getRemainingTime(lockKey, TimeUnit.MINUTES);
        return expiry != null ? expiry : 0L;
    }

    public boolean isAccountLocked(String email) {
        String lockKey = getLockKey(email);
        return Boolean.TRUE.equals(cacheService.hasKey(lockKey));
    }

    private void lockAccount(String email) {
        String lockKey = getLockKey(email);
        cacheService.put(lockKey, Instant.now().getEpochSecond());
    }

    private String getAttemptKey(String email) {
        return "auth:attempt:" + email;
    }

    private String getLockKey(String email) {
        return "auth:locked:" + email;
    }
}
