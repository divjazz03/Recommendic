package com.divjazz.recommendic.externalApi.openFDA;

import com.divjazz.recommendic.externalApi.openFDA.count.OpenFDACountResult;
import com.divjazz.recommendic.externalApi.openFDA.search.OpenFDAApplicationResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class OpenFDAQuery extends OpenFDAConfig{
    public enum Sort {
        ASC,DESC
    }
    RestTemplate restTemplate;

    public OpenFDAQuery(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpenFDAResult query(String countField, Sort sort,String sortTerm, String limit, String Skip, String... searchFields) {
        boolean checkSort = Objects.nonNull(sort) && !Objects.requireNonNull(sortTerm).isEmpty();
        if (Objects.isNull(countField) || countField.isEmpty()){
            StringBuilder url = new StringBuilder(getOpenFDABaseUrl()+getOpenFDADrugsEndPoint());
            url.append("search=");
            long c = Stream.of(searchFields).map((s) -> url.append(s).append("+")).count();
            url.deleteCharAt(url.length()-1);
            if (checkSort) {
                url.append("sort=").append(sortTerm).append(":").append(sort.toString());
            }
            if (!limit.isEmpty()){
                url.append("&").append("limit=").append(limit);
            }
            return restTemplate.getForObject(url.toString(), OpenFDAApplicationResult.class);
        } else {
            StringBuilder url = new StringBuilder(getOpenFDABaseUrl()+getOpenFDADrugsEndPoint());
            url.append("search=");
            long c = Stream.of(searchFields).map((s) -> url.append(s).append("+")).count();
            url.deleteCharAt(url.length()-1);
            if (checkSort) {
                url.append("sort=").append(sortTerm).append(":").append(sort.toString());
            }
            url.append("count=").append(countField);
            if (!limit.isEmpty()){
                url.append("&").append("limit=").append(limit);
            }
            return restTemplate.getForObject(url.toString(), OpenFDACountResult.class);
        }
    }
}
