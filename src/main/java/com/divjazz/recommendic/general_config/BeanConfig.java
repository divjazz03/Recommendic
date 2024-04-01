package com.divjazz.recommendic.general_config;

import com.google.maps.GeoApiContext;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeanConfig {
    Dotenv dotenv;
    public BeanConfig(){
        dotenv = Dotenv
                .load();
    }
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey(dotenv.get("API_KEY"))
                .build();
    }
}
