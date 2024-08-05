package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.cache.CacheStore;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
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
import java.util.UUID;

@Service
public class GeneralUserService {

    private final PatientRepository patientRepository;
    private final ConsultantRepository consultantRepository;
    private final AdminRepository adminRepository;

    private final UserRepository userRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final CacheStore<String, Integer> loginCache;

    public GeneralUserService(PatientRepository patientRepository,
                              ConsultantRepository consultantRepository,
                              AdminRepository adminRepository,
                              UserRepository userRepository, UserCredentialRepository userCredentialRepository, CacheStore<String, Integer> loginCache) {
        this.patientRepository = patientRepository;
        this.consultantRepository = consultantRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.loginCache = loginCache;
    }



    public User retrieveUserByUsername(String username){
        User user = null;
        if (patientRepository.findByEmail(username).isPresent()){
            user = patientRepository.findByEmail(username).get();
        } else if(consultantRepository.findByEmail(username).isPresent()){
            user = consultantRepository.findByEmail(username).get();
        } else if(adminRepository.findByEmail(username).isPresent()){
            user = adminRepository.findByEmail(username).get();
        } else
            throw new UsernameNotFoundException("User not found");

        return user;
    }

    public User retrieveUserByUserId(String id){
        User user = null;
        if (patientRepository.findByUserId(UUID.fromString(id)).isPresent()){
            user = patientRepository.findByUserId(UUID.fromString(id)).get();
        } else if(consultantRepository.findByUserId(UUID.fromString(id)).isPresent()){
            user = consultantRepository.findByUserId(UUID.fromString(id)).get();
        } else if(adminRepository.findByUserId(UUID.fromString(id)).isPresent()){
            user = adminRepository.findByUserId(UUID.fromString(id)).get();
        } else
            throw new UsernameNotFoundException("User not found");

        return user;
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

    public boolean isUserNotExists(String username){
        return !patientRepository.existsByEmail(username) && !consultantRepository.existsByEmail(username) && !adminRepository.existsByEmail(username);
    }
}





















