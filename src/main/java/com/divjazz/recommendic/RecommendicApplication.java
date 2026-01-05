package com.divjazz.recommendic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.divjazz.recommendic.appointment.repository",
        "com.divjazz.recommendic.recommendation.repository",
        "com.divjazz.recommendic.consultation.repository",
        "com.divjazz.recommendic.appointment.repository",
        "com.divjazz.recommendic.security.repository",
        "com.divjazz.recommendic.user.repository",
        "com.divjazz.recommendic.chat.repository",
        "com.divjazz.recommendic.search.repository",
        "com.divjazz.recommendic.article.repository",
        "com.divjazz.recommendic.notification.app.repository",
        "com.divjazz.recommendic.medication.repository"
})
@EntityScan(basePackages = {
        "com.divjazz.recommendic.appointment.model",
        "com.divjazz.recommendic.recommendation.model",
        "com.divjazz.recommendic.consultation.model",
        "com.divjazz.recommendic.appointment.model",
        "com.divjazz.recommendic.security.model",
        "com.divjazz.recommendic.user.model",
        "com.divjazz.recommendic.chat.model",
        "com.divjazz.recommendic.search.model",
        "com.divjazz.recommendic.article.model",
        "com.divjazz.recommendic.notification.app.model",
        "com.divjazz.recommendic.medication.model"
})
@EnableTransactionManagement
@EnableAsync
@EnableCaching
@EnableJpaAuditing
public class RecommendicApplication {
    public final Logger log = LoggerFactory.getLogger(RecommendicApplication.class);

    public RecommendicApplication() {
    }

    static void main(String[] args) {
        SpringApplication.run(RecommendicApplication.class, args);
    }


}