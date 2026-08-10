package com.divjazz.recommendic.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getstream.services.framework.StreamSDKClient;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class BeanConfig {
    private final ApplicationConfigProp appConfig;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean(name = "recommendicTaskExecutor")
    public TaskExecutor recommendicTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("Async-");
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public StreamSDKClient streamSDKClient () {
        var apiSecret = appConfig.ioStream().apiSecret();
        var apiKey = appConfig.ioStream().apiKey();
        return new StreamSDKClient(apiKey, apiSecret);
    }


    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperCustomizer () {
        return (builder) -> builder
                .findModulesViaServiceLoader(true)
                .serializationInclusion(JsonInclude.Include.NON_DEFAULT)
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false)
                .build();
    }

    @Bean
    public OpenAPI recommendicOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recommendic API")
                        .description("A medical recommendation app")
                        .version("v0.0.1")
                        .termsOfService("https://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }

}





