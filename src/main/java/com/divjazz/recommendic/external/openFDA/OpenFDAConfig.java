package com.divjazz.recommendic.external.openFDA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFDAConfig {

    @Value("${api.drug.base_url}")
    private String openFDABaseUrl;
    @Value("${api.drug.adverse_effect_endpoint}")
    private String openFDAAdverseEffectEndpoint;
    @Value("${api.drug.drugs_endpoint}")
    private String openFDADrugsEndPoint;
    @Value("${api.drug.label_endpoint}")
    private String openFDALabelEndPoint;


    public String getOpenFDABaseUrl() {
        return openFDABaseUrl;
    }

    public String getOpenFDAAdverseEffectEndpoint() {
        return openFDAAdverseEffectEndpoint;
    }

    public String getOpenFDADrugsEndPoint() {
        return openFDADrugsEndPoint;
    }

    public String getOpenFDALabelEndPoint() {
        return openFDALabelEndPoint;
    }
}
