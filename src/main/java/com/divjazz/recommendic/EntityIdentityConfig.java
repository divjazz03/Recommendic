package com.divjazz.recommendic;

import io.github.wimdeblauwe.jpearl.InMemoryUniqueIdGenerator;
import io.github.wimdeblauwe.jpearl.UniqueIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class EntityIdentityConfig {
    @Bean
    public UniqueIdGenerator<UUID> uniqueIdGenerator(){
        return new InMemoryUniqueIdGenerator();
    }
}
