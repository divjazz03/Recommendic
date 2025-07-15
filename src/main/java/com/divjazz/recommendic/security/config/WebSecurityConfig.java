package com.divjazz.recommendic.security.config;

import com.divjazz.recommendic.security.CustomAuthenticationProvider;
import com.divjazz.recommendic.security.CustomUserDetailsService;
import com.divjazz.recommendic.security.filter.AuthFilter;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

    @Bean("mainPasswordEncoder")
    @Primary
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean("seedPasswordEncoder")
    public PasswordEncoder seedPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    private static final String[] WHITELIST_PATHS = {"/api/v1/users","/api/v1/medical_categories","/error",
            "/api-docs","/api-docs/*", "/api-docs.yaml","/swagger-ui/*", "/actuator/**", "/favicon.ico",
            "/api/v1/auth/*"
    };

    @Bean
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITELIST_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/consultants","/api/v1/patients").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CustomAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder,
                                                           GeneralUserService generalUserService){
        return new CustomAuthenticationProvider(passwordEncoder, userDetailsService, generalUserService);
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
    CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(
                    List.of(corsFrontendDomain)
            );
            corsConfiguration.setAllowedMethods(
                    List.of("GET", "POST", "PATCH", "DELETE")
            );
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
            configurationSource.registerCorsConfiguration("/**", corsConfiguration);
            return configurationSource;
    }

}
