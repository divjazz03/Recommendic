package com.divjazz.recommendic.media.cloudinary.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.divjazz.recommendic.global.config.ApplicationConfigProp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final ApplicationConfigProp applicationConfigProp;


    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", applicationConfigProp.cloudinary().cloudName(),
                "api_key", applicationConfigProp.cloudinary().apiKey(),
                "api_secret", applicationConfigProp.cloudinary().apiSecret()
        ));
    }
}
