package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
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

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity http,
                                           LoginAuthenticationFilter loginAuthenticationFilter,
                                           JwtAuthenticationFilter filter,
                                           LogoutAuthenticationFilter logoutFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                       .requestMatchers("/api/v1/patient/create",
                        "/api/v1/consultant/create").permitAll()
                        .requestMatchers("/api/v1/search/drug/**", "api/v1/medical_categories/").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                        .requestMatchers("/api-docs","/api-docs/*", "/api-docs.yaml","swagger-ui/*").permitAll()
                        .requestMatchers("/actuator/**", "/favicon.ico").permitAll()
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
                .addFilterBefore(loginAuthenticationFilter, AuthorizationFilter.class)
                .addFilterBefore(filter, AuthorizationFilter.class)
                .addFilterBefore(logoutFilter, AuthorizationFilter.class)
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

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    LogoutAuthenticationFilter logoutAuthenticationFilter(JwtService jwtService) {
        return new LogoutAuthenticationFilter(jwtService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(
                    List.of(corsFrontendDomain)
            );
            corsConfiguration.setAllowedMethods(
                    List.of("GET", "POST", "PUT", "DELETE")
            );
            corsConfiguration.setAllowedHeaders(List.of("*"));
            return corsConfiguration;
        };
    }

}
