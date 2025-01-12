package com.divjazz.recommendic.externalApi.openFDA;

import com.divjazz.recommendic.externalApi.openFDA.count.OpenFDACountResult;
import com.divjazz.recommendic.externalApi.openFDA.search.OpenFDAApplicationResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OpenFDAQuery extends OpenFDAConfig{
    public enum Sort {
        ASC,DESC
    }
    private final RestTemplate restTemplate;
    private final List<String> queryParameters;

    public OpenFDAQuery(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.queryParameters = List.of(Arrays.toString(OpenFDAQueryParameters.values()));
    }

    public OpenFDAResult queryDrugs(@NotNull String searchParam, List<String> searchTerms) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam,url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(@NotNull String searchParam, List<String> searchTerms, String countField) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam,url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("limit=").append(5);
        url.append("&").append("count=").append(countField);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }

    public OpenFDAResult queryDrugs(Sort sort, @NotNull String searchParam, List<String> searchTerms) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("sort=").append(sort.toString());
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(Sort sort, @NotNull String searchParam, List<String> searchTerms, String countField) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("sort=").append(sort.toString());
        url.append("&").append("limit=").append(5);
        url.append("&").append("count=").append(countField);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(int limit, @NotNull String searchParam, List<String> searchTerms) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("limit=").append(limit);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(int limit, @NotNull String searchParam, List<String> searchTerms, String countField) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("limit=").append(limit);
        url.append("&").append("count=").append(countField);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(Sort sort, int limit, @NotNull String searchParam, List<String> searchTerms) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("sort=").append(sort.toString());
        url.append("&").append("limit=").append(limit);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }
    public OpenFDAResult queryDrugs(Sort sort, int limit, @NotNull String searchParam, List<String> searchTerms, String countField) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("sort=").append(sort.toString());
        url.append("&").append("limit=").append(limit);
        url.append("&").append("count=").append(countField);
        return restTemplate.getForObject(url.toString(),OpenFDAApplicationResult.class);
    }

    public OpenFDAResult queryDrugs(
                                    Sort sort,
                                    String sortTerm,
                                    @NotNull String searchParam,
                                    List<String> searchTerms,
                                    String countField) {
        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
        url.append("search=");
        appendSearchParam(searchParam, url);
        if (searchTerms.size() > 1) {
            searchTerms.stream().skip(1).forEach(url::append);
            url.delete(url.length()-4, url.length());
        }
        url.append(searchParam);
        url.append("&").append("sort=").append(sortTerm).append(":").append(sort.toString());
        url.append("&").append("count=").append(countField);
        url.append("&").append("limit=").append(5);
        return restTemplate.getForObject(url.toString(),OpenFDACountResult.class);
    }

    private void appendSearchParam(@NotNull String searchParam, StringBuilder url) {
        var fdaSearchParam = convertToOpenFDAParameter(searchParam);
        url.append((Objects.nonNull(fdaSearchParam))? fdaSearchParam.getParam() : "");
    }
    //  public OpenFDAResult queryAdverseEffect(){}
    private List<OpenFDAQueryParameters> convertToOpenFDAParameters(List<String> params) {

        return params.stream()
                .filter(queryParameters::contains)
                .map(OpenFDAQueryParameters::valueOf)
                .collect(Collectors.toList());
    }
    private OpenFDAQueryParameters convertToOpenFDAParameter(String param) {
        return (queryParameters.contains(param))? OpenFDAQueryParameters.valueOf(param):null;
    }

}
