package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.cache.CacheStore;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class GeneralUserService {

    private final UserRepository userRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final CacheStore<String, Integer> loginCache;

    public GeneralUserService(
                              UserRepository userRepository, UserCredentialRepository userCredentialRepository, CacheStore<String, Integer> loginCache) {

        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.loginCache = loginCache;
    }



    public User retrieveUserByUsername(String username){
        User user = null;
        if (userRepository.findByEmail(username).isPresent()){
            user = userRepository.findByEmail(username).get();
        } else
            throw new UsernameNotFoundException("User not found");
        return user;
    }

    public Optional<User> retrieveUserByUserId(String id){

        return userRepository.findByUserId(id);
    }

    public User retrieveUserById(Long id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public UserCredential retrieveCredentialById(Long id) {
        return userCredentialRepository.findById(id).orElseThrow(() -> new RuntimeException("Credentials not Found"));
    }
    public void updateLoginAttempt(String email, LoginType loginType){
        var user = retrieveUserByUsername(email);
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (loginCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                loginCache.put(user.getEmail(), user.getLoginAttempts());
                if (loginCache.get(user.getEmail()) > 5) {
                    user.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setAccountNonLocked(true);
                user.setLoginAttempts(0);
                user.setLastLogin(LocalDateTime.now());
                loginCache.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }

    public boolean isUserNotExists(String email){
        return !userRepository.existsByEmail(email);
    }
}





















