package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final GeneralUserService userService;

    private final JwtService jwtService;
    public WebSecurityConfig(GeneralUserService userService, JwtService service) {
        this.userService = userService;
        this.jwtService = service;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity http,AuthenticationManager authenticationManager, AuthenticationFilter authenticationFilter,JwtAuthenticationFilter filter) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("api/v1/patient/create",
                                "api/v1/consultant/consultant", "/user/login").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(filter, AuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(GeneralUserService userService, PasswordEncoder passwordEncoder, UserCredentialRepository userCredentialRepository){
        var provider = new CustomAuthenticationProvider(userService, passwordEncoder, userCredentialRepository);
        return new ProviderManager(provider);
    }
    @Bean
    UserDetailsService userDetailsService (){
        return new CustomUserDetailsService(userService);
    }

    @Bean
    AuthenticationFilter authenticationFilter(JwtService service, AuthenticationManager authenticationManager, GeneralUserService userService) {
        return new AuthenticationFilter(authenticationManager, service,userService);
    }

    @Bean()
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, GeneralUserService userService) {
        return new JwtAuthenticationFilter(jwtService,userService);
    }




}
