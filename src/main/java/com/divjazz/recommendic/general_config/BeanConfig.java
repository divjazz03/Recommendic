package com.divjazz.recommendic.general_config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeanConfig {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey("")
                .build();
    }
}
