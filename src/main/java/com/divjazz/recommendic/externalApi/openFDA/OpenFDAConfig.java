package com.divjazz.recommendic.externalApi.openFDA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenFDAConfig {

    @Value("${api.drug.base_url}")
    String openFDABaseUrl;
    @Value("${api.drug.adverse_effect_endpoint}")
    String openFDAAdverseEffectEndpoint;
    @Value("${api.drug.drugs_endpoint}")
    String openFDADrugsEndPoint;


    public String getOpenFDABaseUrl() {
        return openFDABaseUrl;
    }

    public String getOpenFDAAdverseEffectEndpoint() {
        return openFDAAdverseEffectEndpoint;
    }

    public String getOpenFDADrugsEndPoint() {
        return openFDADrugsEndPoint;
    }
}
