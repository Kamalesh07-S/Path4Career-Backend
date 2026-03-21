package com.careerpath.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class AdzunaService {

    private final RestTemplate restTemplate;

    @Value("${adzuna.api.app-id}")
    private String appId;

    @Value("${adzuna.api.app-key}")
    private String appKey;

    @Value("${adzuna.api.base-url}")
    private String baseUrl;

    public AdzunaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Search jobs from the Adzuna API.
     *
     * @param keyword        search keyword (e.g. "javascript developer")
     * @param page           page number (1-based)
     * @param resultsPerPage number of results per page
     * @param country        country code (e.g. "in" for India)
     * @return raw JSON response from Adzuna as a Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> searchJobs(String keyword, int page, int resultsPerPage, String country) {

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment("jobs", country, "search", String.valueOf(page))
                .queryParam("app_id", appId)
                .queryParam("app_key", appKey)
                .queryParam("results_per_page", resultsPerPage)
                .queryParam("what", keyword)
                .queryParam("content-type", "application/json")
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
