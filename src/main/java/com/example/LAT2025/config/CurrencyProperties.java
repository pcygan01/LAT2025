package com.example.LAT2025.config;

import com.example.LAT2025.model.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app.currency")
public class CurrencyProperties {
    
    private long refreshRateMs = 3600000;
    private boolean useExternalApi = true;
    private String apiKey = "";
    private Currency baseCurrency = Currency.PLN;
    
    public long getRefreshRateMs() {
        return refreshRateMs;
    }
    
    public void setRefreshRateMs(long refreshRateMs) {
        this.refreshRateMs = refreshRateMs;
    }
    
    public boolean isUseExternalApi() {
        return useExternalApi;
    }
    
    public void setUseExternalApi(boolean useExternalApi) {
        this.useExternalApi = useExternalApi;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public Currency getBaseCurrency() {
        return baseCurrency;
    }
    
    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
} 