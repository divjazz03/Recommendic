package com.divjazz.recommendic.externalApi.openFDA;

import com.divjazz.recommendic.externalApi.openFDA.count.OpenFDACountResult;
import com.divjazz.recommendic.externalApi.openFDA.search.OpenFDAApplicationResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class OpenFDAQuery extends OpenFDAConfig{
    public enum Sort {
        ASC,DESC
    }
    private final RestTemplate restTemplate;

    public OpenFDAQuery(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpenFDAResult queryDrugs(@NotNull List<String> searchFields) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        for (String searchField : searchFields) {
            url.append(searchField).append("AND");
        }
        url.delete(url.length()-3, url.length());
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(String countField, @NotNull List<String> searchFields) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        for (String searchField : searchFields) {
            url.append(searchField).append("AND");
        }
        url.delete(url.length()-4, url.length());
        url.append("&").append("count=").append(countField);
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDACountResult.class);
    }
    public OpenFDAResult queryDrugs(Sort sort, String sortTerm, @NotNull List<String> searchFields) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        for (String searchField : searchFields) {
            url.append(searchField).append("AND");
        }
        url.delete(url.length()-4, url.length());
        url.append("&").append("sort=").append(sortTerm);
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(String countField, Sort sort, String sortTerm, @NotNull List<String> searchFields) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        for (String searchField : searchFields) {
            url.append(searchField).append("AND");
        }
        url.delete(url.length()-4, url.length());
        url.append("&").append("sort=").append(sortTerm).append(":").append(sort.toString());
        url.append("&").append("count=").append(countField);
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDACountResult.class);
    }
  //  public OpenFDAResult queryAdverseEffect(){}

}
