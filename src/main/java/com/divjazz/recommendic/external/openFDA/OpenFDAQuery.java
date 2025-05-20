package com.divjazz.recommendic.external.openFDA;

import com.divjazz.recommendic.external.openFDA.count.OpenFDACountResultWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenFDAQuery extends OpenFDAConfig {
    private final RestTemplate restTemplate;

    public OpenFDAQuery(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpenFDAResult queryDrugs(Sort sort, int limit, Map<String, String> searchFieldTerm, String countField) {
//        StringBuilder url = new StringBuilder(getOpenFDABaseUrl() + getOpenFDADrugsEndPoint());
//        boolean sortAndCountAndSearchFieldIsNull = Objects.isNull(sort) && Objects.isNull(countField) && Objects.isNull(searchFieldTerm);
//        boolean sortAndCountNullButSearchFieldNotNull = Objects.isNull(sort) && Objects.isNull(countField) && Objects.nonNull(searchFieldTerm);
//        boolean sortAndSearchFieldIsNullButCountNotNull = Objects.isNull(sort) && Objects.isNull(searchFieldTerm) && Objects.nonNull(countField);
//        boolean countAndSearchIsNullButSortNotNull = Objects.isNull(countField) && Objects.isNull(searchFieldTerm) && Objects.nonNull(sort);
//        boolean countNullButSortAndSearchFieldNotNull = Objects.isNull(countField) && Objects.nonNull(searchFieldTerm) && Objects.nonNull(sort);
//
//        if (sortAndCountAndSearchFieldIsNull) {
//            url.append("limit=").append(limit);
//            return restTemplate.getForObject(url.toString(), OpenFDADrugResult.class);
//        }
//        if (sortAndCountNullButSearchFieldNotNull) {
//            url.append("search=");
//            appendDrugSearchParamToUrl(searchFieldTerm, url);
//            url.append("&");
//            url.append("limit=").append(limit);
//            return restTemplate.getForObject(url.toString(), OpenFDADrugResult.class);
//        }
//        if (sortAndSearchFieldIsNullButCountNotNull) {
//            url.append("count=").append(countField);
//            url.append("&");
//            url.append("limit=").append(limit);
//            return restTemplate.getForObject(url.toString(), OpenFDACountResultWrapper.class);
//        }
//        if (countNullButSortAndSearchFieldNotNull) {
//            url.append("search=");
//            url.append("&").append("sort=").append(sort);
//            appendDrugSearchParamToUrl(searchFieldTerm, url);
//            url.append("&");
//            url.append("limit=").append(limit);
//            return restTemplate.getForObject(url.toString(), OpenFDADrugResult.class);
//        }
//        if (countAndSearchIsNullButSortNotNull) {
//            url.append("sort=").append(sort);
//            url.append("&").append("limit=").append(limit);
//            return restTemplate.getForObject(url.toString(), OpenFDADrugResult.class);
//        }
//        return null;

        StringBuilder url = new StringBuilder(getOpenFDABaseUrl()).append(getOpenFDADrugsEndPoint());
        List<String> queryParams = new ArrayList<>();

        if (searchFieldTerm != null && !searchFieldTerm.isEmpty()) {
            var searchParam = buildDrugSearchParamToUrl(searchFieldTerm);
            queryParams.add("search=" + searchParam);
        }
        if (sort != null) {
            queryParams.add("sort=" + sort);
        }
        if (countField != null) {
            queryParams.add("count=" + countField);
        }

        queryParams.add("limit=" + limit);
        url.append(String.join("&", queryParams));
        if (countField == null) {
            return restTemplate.getForObject(url.toString(), OpenFDADrugResult.class);
        } else {
            return restTemplate.getForObject(url.toString(), OpenFDACountResultWrapper.class);
        }

    }

    public OpenFDAResult queryLabels(Sort sort, int limit, Map<String, String> searchFieldTerm, String countField) {
       StringBuilder url = new StringBuilder(getOpenFDABaseUrl()).append(getOpenFDALabelEndPoint());
       List<String> params = new ArrayList<>();

       if (searchFieldTerm != null) {
           String searchParam = buildLabelSearchParamToUrl(searchFieldTerm);
           params.add("search=" + searchParam);
       }

       if (sort != null) {
           params.add("sort=" + sort.name());
       }
       if (countField != null && !countField.isEmpty()) {
           params.add("count=" + countField);
       }

       params.add("limit=" + limit);
       url.append(String.join("&",params));

       if (countField != null) {
           return restTemplate.getForObject(url.toString(), OpenFDACountResultWrapper.class);
       } else {
           return restTemplate.getForObject(url.toString(), OpenFDALabelResult.class);
       }
    }

    private String buildDrugSearchParamToUrl(Map<String, String> searchFieldTerm) {
        return searchFieldTerm.entrySet().stream()
                .map(entry -> {
                    OpenFDADrugQueryParameters fdaSearchParam = convertToOpenFDADrugQueryParameter(entry.getKey());
                    return fdaSearchParam != null ? "%s:%s".formatted(fdaSearchParam.getValue(), entry.getValue()) : "";
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("+AND+"));
    }

    private String buildLabelSearchParamToUrl(Map<String, String> searchFieldTerm) {
        return searchFieldTerm.entrySet().stream()
                .map(entry -> {
                    OpenFDALabelQueryParams fdaSearchParam = convertToOpenFDALabelQueryParameter(entry.getKey());
                    return fdaSearchParam != null ? "%s:%s".formatted(fdaSearchParam.getValue(), entry.getValue()) : "";
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("+AND+"));
    }

    private OpenFDADrugQueryParameters convertToOpenFDADrugQueryParameter(String param) {
        var queryParameters = Arrays.stream(OpenFDADrugQueryParameters.values())
                .map(OpenFDADrugQueryParameters::getValue)
                .toList();
        return (queryParameters.contains(param)) ? OpenFDADrugQueryParameters.fromValue(param) : null;
    }

    private OpenFDALabelQueryParams convertToOpenFDALabelQueryParameter(String param) {
        var queryParameters = Arrays.stream(OpenFDALabelQueryParams.values())
                .map(OpenFDALabelQueryParams::getValue)
                .toList();
        return (queryParameters.contains(param)) ? OpenFDALabelQueryParams.fromValue(param) : null;
    }

    public enum Sort {
        ASC, DESC
    }

}
