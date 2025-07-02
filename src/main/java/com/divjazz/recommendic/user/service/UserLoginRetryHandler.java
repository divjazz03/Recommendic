package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.cache.service.CacheService;
import com.divjazz.recommendic.global.security.exception.LoginFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class UserLoginRetryHandler {

    @Value("${auth.maxAttempts}")
    private int MAX_ATTEMPTS;

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
        int currentAttempts = (int) cacheService.get(attemptKey);
        if (currentAttempts == 0) {
            currentAttempts = 1;
            cacheService.put(attemptKey, currentAttempts);
            return currentAttempts;
        }
        cacheService.put(attemptKey,++currentAttempts);
        return currentAttempts;
    }

    private int getFailedAttempts(String email) {
        String attemptKey = getAttemptKey(email);
        return (int) cacheService.get(attemptKey);
    }

    private long getRemainingLockTime(String email) {
        String lockKey = getLockKey(email);
        return cacheService.getRemainingTime(lockKey, TimeUnit.MINUTES);
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
