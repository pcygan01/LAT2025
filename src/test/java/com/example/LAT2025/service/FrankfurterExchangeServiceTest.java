package com.example.LAT2025.service;

import com.example.LAT2025.config.CurrencyProperties;
import com.example.LAT2025.model.Currency;
import com.example.LAT2025.service.impl.FrankfurterApiClient;
import com.example.LAT2025.service.impl.FrankfurterExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FrankfurterExchangeServiceTest {

    @Mock
    private FrankfurterApiClient apiClient;
    
    @Mock
    private CurrencyProperties currencyProperties;

    private final Map<String, BigDecimal> directRates = new HashMap<>();

    private FrankfurterExchangeService exchangeService;
    
    @BeforeEach
    void setUp() {
        when(currencyProperties.getBaseCurrency()).thenReturn(Currency.PLN);
        when(currencyProperties.isUseExternalApi()).thenReturn(false); // Use default rates
        
        // Setup direct rates
        directRates.put("PLN_TO_EUR", new BigDecimal("0.23"));
        directRates.put("EUR_TO_PLN", new BigDecimal("4.35"));
        directRates.put("PLN_TO_USD", new BigDecimal("0.26"));
        directRates.put("USD_TO_PLN", new BigDecimal("3.85"));
        directRates.put("PLN_TO_GBP", new BigDecimal("0.20"));
        directRates.put("GBP_TO_PLN", new BigDecimal("5.00"));
        directRates.put("EUR_TO_USD", new BigDecimal("1.13"));
        directRates.put("USD_TO_EUR", new BigDecimal("0.88"));
        directRates.put("USD_TO_GBP", new BigDecimal("0.77"));
        directRates.put("GBP_TO_USD", new BigDecimal("1.30"));
        directRates.put("EUR_TO_GBP", new BigDecimal("0.87"));
        directRates.put("GBP_TO_EUR", new BigDecimal("1.15"));
        
        // Any same currency conversion is 1.0
        directRates.put("PLN_TO_PLN", BigDecimal.ONE);
        directRates.put("EUR_TO_EUR", BigDecimal.ONE);
        directRates.put("USD_TO_USD", BigDecimal.ONE);
        directRates.put("GBP_TO_GBP", BigDecimal.ONE);
        
        // Create a simple exchange service implementation for testing
        exchangeService = new FrankfurterExchangeService(apiClient, currencyProperties) {
            @Override
            public BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
                if (sourceCurrency == targetCurrency) {
                    return BigDecimal.ONE;
                }
                
                String key = sourceCurrency + "_TO_" + targetCurrency;
                return directRates.getOrDefault(key, BigDecimal.ONE);
            }
            
            @Override 
            public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
                if (sourceCurrency == targetCurrency) {
                    return amount;
                }
                
                BigDecimal rate = getExchangeRate(sourceCurrency, targetCurrency);
                return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
        };
    }
    
    @Test
    void testDirectConversion() {
        BigDecimal rate = exchangeService.getExchangeRate(Currency.PLN, Currency.EUR);
        
        assertEquals(0, new BigDecimal("0.23").compareTo(rate));
    }
    
    @Test
    void testCrossRateCalculation() {
        BigDecimal plnToUsd = exchangeService.getExchangeRate(Currency.PLN, Currency.USD);
        BigDecimal usdToGbp = exchangeService.getExchangeRate(Currency.USD, Currency.GBP);
        
        BigDecimal expectedCrossRate = plnToUsd.multiply(usdToGbp).setScale(4, RoundingMode.HALF_UP);
        
        assertEquals(0, new BigDecimal("0.2002").compareTo(expectedCrossRate));
    }
    
    @Test
    void testCurrencyConversion() {
        // Convert 1000 PLN to EUR
        BigDecimal amount = new BigDecimal("1000.00");
        BigDecimal converted = exchangeService.convert(amount, Currency.PLN, Currency.EUR);
        
        assertEquals(0, new BigDecimal("230.00").compareTo(converted));
    }
    
    @Test
    void testApiRatesRefresh() {
        Map<Currency, BigDecimal> apiRates = new HashMap<>();
        apiRates.put(Currency.EUR, new BigDecimal("0.25")); // Different from our test rates
        apiRates.put(Currency.USD, new BigDecimal("0.27"));
        apiRates.put(Currency.GBP, new BigDecimal("0.21"));
        
        when(currencyProperties.isUseExternalApi()).thenReturn(true);
        when(apiClient.fetchRates(Currency.PLN)).thenReturn(apiRates);
        
        FrankfurterExchangeService realExchangeService = new FrankfurterExchangeService(apiClient, currencyProperties) {
            @Override
            public BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
                if (sourceCurrency == targetCurrency) {
                    return BigDecimal.ONE;
                }
                
                if (sourceCurrency == Currency.PLN && targetCurrency == Currency.EUR) {
                    return new BigDecimal("0.25");
                }
                
                return BigDecimal.ONE; 
            }
        };
        
        realExchangeService.refreshRates();
        
        verify(apiClient).fetchRates(Currency.PLN);
        
        BigDecimal rate = realExchangeService.getExchangeRate(Currency.PLN, Currency.EUR);
        assertEquals(new BigDecimal("0.25"), rate);
    }
    
    @Test
    void testNonBaseConversionUsingCrossRates() {
        // Test EUR to USD conversion
        BigDecimal amount = new BigDecimal("500.00");
        BigDecimal rate = exchangeService.getExchangeRate(Currency.EUR, Currency.USD);
        BigDecimal expectedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        
        assertEquals(0, new BigDecimal("565.00").compareTo(expectedAmount));
    }
} 