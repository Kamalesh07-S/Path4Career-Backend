package com.careerpath.admin.dto;

public class AdzunaSearchRequest {

    private String keyword;
    private String country = "in";
    private int page = 1;
    private int resultsPerPage = 20;

    public AdzunaSearchRequest() {
    }

    // ===== Getters and Setters =====

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}
