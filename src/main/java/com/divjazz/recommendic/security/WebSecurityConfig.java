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

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final GeneralUserService userService;

    public WebSecurityConfig(GeneralUserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity http,
                                           LoginAuthenticationFilter loginAuthenticationFilter,
                                           JwtAuthenticationFilter filter) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("api/v1/patient/create",
                        "api/v1/consultant/create",
                                "api/v1/search").permitAll()
                        .requestMatchers("api/patient/delete").hasAnyRole("PATIENT","ADMIN","SUPER_ADMIN")
                        .requestMatchers("api/patient/patients").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("api/patient/search").hasRole("PATIENT")
                        .requestMatchers("api/patient/recommendations").hasRole("PATIENT")
                        .requestMatchers("api/v1/consultant/consultants").hasAnyRole("CONSULTANT","ADMIN","SUPER_ADMIN")
                        .requestMatchers("api/v1/admin/create/").hasRole("SUPER_ADMIN")
                        .requestMatchers("api/v1/admin/admins").hasRole("SUPER_ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
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
    LoginAuthenticationFilter authenticationFilter(JwtService service, AuthenticationManager authenticationManager, GeneralUserService userService) {
        return new LoginAuthenticationFilter(authenticationManager, service,userService);
    }

    @Bean()
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }




}
