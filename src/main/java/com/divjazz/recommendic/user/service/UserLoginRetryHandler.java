package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.cache.service.CacheService;
import com.divjazz.recommendic.global.config.ApplicationConfigProp;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class UserLoginRetryHandler {


    private final CacheService<Object, Object> cacheService;
    private final Integer max_attempts;

    public UserLoginRetryHandler(CacheService<Object, Object> cacheService, ApplicationConfigProp configProp) {
        this.cacheService = cacheService;
        this.max_attempts = configProp.auth().maxAttempts();
    }

    public void handleFailedAttempts(String email) {
        var failedAttempts = incrementFailedAttempt(email);
        var remainingAttempts = max_attempts - Math.min(max_attempts, failedAttempts);
        if (failedAttempts >= max_attempts) {
            lockAccount(email);
            throw new LockedException(
                    "Your account has been locked due to too many failed attempts. Please try again in %d minutes".formatted(getRemainingLockTime(email)));
        }
        throw new LoginFailedException(
                "Invalid credentials: You have failed %s time(s), %s attempts left"
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
        Integer currentAttempts = (Integer) cacheService.get(attemptKey);
        if (currentAttempts == null) {
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
        return cacheService.hasKey(lockKey);
    }

    private void lockAccount(String email) {
        String lockKey = getLockKey(email);
        cacheService.put(lockKey, Instant.now().getEpochSecond());
    }

    private String getAttemptKey(String email) {
        return "auth:attempt:%s".formatted(email);
    }

    private String getLockKey(String email) {
        return "auth:locked:%s".formatted(email);
    }
}
