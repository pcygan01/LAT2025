package com.example.LAT2025.model.api;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FrankfurterResponse {
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;
    
    public FrankfurterResponse() {
    }
    
    public String getBase() {
        return base;
    }
    
    public void setBase(String base) {
        this.base = base;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Map<String, BigDecimal> getRates() {
        return rates;
    }
    
    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
} 