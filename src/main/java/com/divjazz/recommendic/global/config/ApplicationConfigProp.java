package com.divjazz.recommendic.global.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "app")
public record ApplicationConfigProp(
        CloudinaryConfiguration cloudinary,
        AuthProperties auth) {


    public record CloudinaryConfiguration(
            String cloudName,
            String apiKey,
            String apiSecret
    ){}
    public record AuthProperties(
            Integer maxAttempts
            , Rsa rsa
    ){}
    public record Rsa(Resource privateKey, Resource publicKey){}
}
