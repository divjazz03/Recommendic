package com.divjazz.recommendic.general_config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Configuration
public class BeanConfig {

    public BeanConfig(){

    }
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GeoApiContext geoApiContext(){
        return null;
    }

    @Bean
    public AlternativeJdkIdGenerator randomIdGenerator(){
        return new AlternativeJdkIdGenerator();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

