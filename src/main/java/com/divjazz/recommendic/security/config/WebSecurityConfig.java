package com.divjazz.recommendic.security.config;

import com.divjazz.recommendic.security.CustomAuthenticationProvider;
import com.divjazz.recommendic.security.CustomUserDetailsService;
import com.divjazz.recommendic.security.filter.AuthFilter;
import com.divjazz.recommendic.security.filter.BaseAuthFilter;
import com.divjazz.recommendic.security.filter.TestAuthFilter;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

    @Getter
    private static final List<String> WHITELIST_PATHS = List.of(
            "/api/v1/medical-categories","/error",
            "/api-docs","/api-docs/*", "/api-docs.yaml","/swagger-ui/*",
            "/actuator/**", "/favicon.ico", "/api/v1/auth/*");
    @Getter
    private static final List<String> NoAuthPostPaths = List.of(
            "/api/v1/consultants","/api/v1/patients"
    );

    @Bean
    @Primary
    @Profile("dev,prod")
    public SecurityFilterChain webSecurity(HttpSecurity http,
                                           BaseAuthFilter authFilter) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITELIST_PATHS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, NoAuthPostPaths.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterAfter(authFilter, SecurityContextHolderFilter.class)
                .build();
    }
    @Bean
    @Profile("test")
    public SecurityFilterChain testSecurity(HttpSecurity http,
                                           BaseAuthFilter authFilter) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITELIST_PATHS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, NoAuthPostPaths.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/users").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterAfter(authFilter, SecurityContextHolderFilter.class)
                .build();
    }

    @Bean
    CustomAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder,
                                                           GeneralUserService generalUserService){
        return new CustomAuthenticationProvider(passwordEncoder, userDetailsService, generalUserService);
    }

    @Bean
    @Profile("dev,prod")
    BaseAuthFilter authFilter(ObjectMapper ob) {
        return new AuthFilter(ob);
    }
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean(CorsConfigurationSource configurationSource) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(configurationSource));
        bean.setOrder(-102);
        return bean;
    }

    @Bean
    @Profile("test")
    BaseAuthFilter testAuthFilter() {
        return new TestAuthFilter();
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
    @Primary
    CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(
                    List.of(corsFrontendDomain)
            );
            corsConfiguration.setAllowedMethods(
                    List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS")
            );
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setMaxAge(3600L);

            UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
            configurationSource.registerCorsConfiguration("/**", corsConfiguration);
            return configurationSource;
    }

}
