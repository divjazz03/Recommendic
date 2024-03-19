package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepositoryCustom userRepository;

    public AppUserDetailsService(UserRepositoryCustom userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not registered"));
    }
}
