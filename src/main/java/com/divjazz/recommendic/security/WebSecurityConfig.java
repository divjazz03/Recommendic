package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${cors.frontend.domain}")
    private String[] corsFrontendDomain;
    private final GeneralUserService userService;

    public WebSecurityConfig(GeneralUserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private static final String[] WHITELIST_PATHS = {"/api/v1/patient/create",
            "/api/v1/consultant/create","/api/v1/search/drug/**", "api/v1/medical_categories/","/error",
            "/api-docs","/api-docs/*", "/api-docs.yaml","swagger-ui/*", "/actuator/**", "/favicon.ico",
            "/api/v1/auth/*"
    };

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity http,
                                           JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITELIST_PATHS).permitAll()
                        .requestMatchers("/api/v1/patient/delete").
                        hasAnyAuthority("PATIENT","ADMIN","SUPER_ADMIN, SYSTEM")
                        .requestMatchers("/api/v1/patient/patients").
                        hasAnyAuthority("PATIENT", "ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/v1/patient/recommendations").
                        hasAuthority("PATIENT")
                        .requestMatchers("/api/v1/consultant/consultants")
                        .hasAnyAuthority("PATIENT","ADMIN","SUPER_ADMIN")
                        .requestMatchers("/api/v1/admin/create").hasAuthority("SUPER_ADMIN")
                        .requestMatchers("/api/v1/admin/admins").hasAuthority("SUPER_ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        return new CustomAuthenticationProvider(passwordEncoder, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
        return new ProviderManager(provider);
    }
    @Bean
    UserDetailsService userDetailsService (){
        return new CustomUserDetailsService(userService);
    }


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(
                    List.of(corsFrontendDomain)
            );
            corsConfiguration.setAllowedMethods(
                    List.of("GET", "POST", "PUT", "DELETE")
            );
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
            configurationSource.registerCorsConfiguration("/**", corsConfiguration);
            return configurationSource;
    }

}
