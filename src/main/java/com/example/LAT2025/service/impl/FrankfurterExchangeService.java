package com.example.LAT2025.service.impl;

import com.example.LAT2025.config.CurrencyProperties;
import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.CurrencyPair;
import com.example.LAT2025.service.CurrencyExchangeService;
import com.example.LAT2025.util.CurrencyRateCalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Currency exchange service using the Frankfurter API for exchange rates
 */
@Service
public class FrankfurterExchangeService implements CurrencyExchangeService {
    
    private static final Logger logger = LoggerFactory.getLogger(FrankfurterExchangeService.class);
    
    private final FrankfurterApiClient apiClient;
    private final CurrencyProperties currencyProperties;
    private final Map<CurrencyPair, BigDecimal> rateCache = new ConcurrentHashMap<>();
    
    // Default rates with PLN as base
    private static final Map<Currency, BigDecimal> DEFAULT_RATES = new HashMap<>();
    
    static {
        // PLN to other currencies (PLN is 1.0)
        DEFAULT_RATES.put(Currency.EUR, new BigDecimal("0.23"));
        DEFAULT_RATES.put(Currency.USD, new BigDecimal("0.26"));
        DEFAULT_RATES.put(Currency.GBP, new BigDecimal("0.20"));
        DEFAULT_RATES.put(Currency.PLN, BigDecimal.ONE);
    }
    
    @Autowired
    public FrankfurterExchangeService(FrankfurterApiClient apiClient, CurrencyProperties currencyProperties) {
        this.apiClient = apiClient;
        this.currencyProperties = currencyProperties;
    }
    
    @PostConstruct
    public void init() {
        initializeDefaultRates();
        
        // If external API is enabled, refresh rates
        if (currencyProperties.isUseExternalApi()) {
            refreshRates();
        }
    }

    private Currency getBaseCurrency() {
        return currencyProperties.getBaseCurrency();
    }
    
    private void initializeDefaultRates() {
        Map<Currency, BigDecimal> adjustedRates = new HashMap<>(DEFAULT_RATES);
        
        Currency baseCurrency = getBaseCurrency();
        if (baseCurrency != Currency.PLN) {
            BigDecimal baseToPlnRate = DEFAULT_RATES.get(baseCurrency);
            
            if (baseToPlnRate != null) {
                for (Currency currency : Currency.values()) {
                    BigDecimal plnToCurrencyRate = DEFAULT_RATES.getOrDefault(currency, BigDecimal.ONE);
                    BigDecimal adjustedRate = plnToCurrencyRate.divide(baseToPlnRate, 6, RoundingMode.HALF_UP);
                    adjustedRates.put(currency, adjustedRate);
                }
            }
        }
        
        adjustedRates.put(baseCurrency, BigDecimal.ONE);
        
        CurrencyRateCalculator.initializeRates(rateCache, baseCurrency, adjustedRates);
    }
    
    @Override
    @Scheduled(fixedDelayString = "${app.currency.refreshRateMs:3600000}")
    public void refreshRates() {
        if (!currencyProperties.isUseExternalApi()) {
            logger.info("External API is disabled, using default rates");
            return;
        }
        
        Currency baseCurrency = getBaseCurrency();
        logger.info("Refreshing currency exchange rates from Frankfurter API with base currency: {}", baseCurrency);
        
        try {
            Map<Currency, BigDecimal> apiRates = apiClient.fetchRates(baseCurrency);
            
            if (apiRates != null && !apiRates.isEmpty()) {
                apiRates.put(baseCurrency, BigDecimal.ONE);
                
                rateCache.clear();
                CurrencyRateCalculator.initializeRates(rateCache, baseCurrency, apiRates);
                
                logger.info("Successfully refreshed currency exchange rates");
            } else {
                logger.warn("Failed to get rates from API, falling back to default rates");
                initializeDefaultRates();
            }
        } catch (Exception e) {
            logger.error("Failed to refresh currency exchange rates: {}", e.getMessage());
            initializeDefaultRates();
        }
    }
    
    @Override
    public BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
        if (sourceCurrency == targetCurrency) {
            return BigDecimal.ONE;
        }
        
        CurrencyPair pair = new CurrencyPair(sourceCurrency, targetCurrency);
        
        // Use cached rate if available
        if (rateCache.containsKey(pair)) {
            return rateCache.get(pair);
        }
        
        // If rate not in cache, recalculate via base currency
        Currency baseCurrency = getBaseCurrency();
        logger.warn("Rate for {}/{} not found in cache, calculating via {}", 
                sourceCurrency, targetCurrency, baseCurrency);
        
        BigDecimal sourceToBase = getExchangeRate(sourceCurrency, baseCurrency);
        BigDecimal baseToTarget = getExchangeRate(baseCurrency, targetCurrency);
        
        BigDecimal crossRate = CurrencyRateCalculator.calculateCrossRate(sourceToBase, baseToTarget);
        
        // Cache the calculated rate
        rateCache.put(pair, crossRate);
        
        return crossRate;
    }
    
    @Override
    public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        if (sourceCurrency == targetCurrency) {
            return amount;
        }
        
        BigDecimal rate = getExchangeRate(sourceCurrency, targetCurrency);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
} 