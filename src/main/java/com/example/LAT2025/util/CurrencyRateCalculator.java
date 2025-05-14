package com.example.LAT2025.util;

import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.CurrencyPair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Utility class for currency rate calculations
 */
public final class CurrencyRateCalculator {
    
    private CurrencyRateCalculator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
 
    public static BigDecimal calculateCrossRate(BigDecimal sourceToBase, BigDecimal baseToTarget) {
        return baseToTarget.multiply(sourceToBase);
    }

    public static void initializeRates(
            Map<CurrencyPair, BigDecimal> rateCache,
            Currency baseCurrency,
            Map<Currency, BigDecimal> baseCurrencyRates) {
        
        rateCache.clear();
        
        for (Currency currency : Currency.values()) {
            rateCache.put(new CurrencyPair(currency, currency), BigDecimal.ONE);
        }
        
        for (Map.Entry<Currency, BigDecimal> entry : baseCurrencyRates.entrySet()) {
            Currency target = entry.getKey();
            if (target == baseCurrency) continue;
            
            BigDecimal baseToTargetRate = entry.getValue();
            rateCache.put(new CurrencyPair(baseCurrency, target), baseToTargetRate);
            
            BigDecimal targetToBaseRate = BigDecimal.ONE.divide(baseToTargetRate, 6, RoundingMode.HALF_UP);
            rateCache.put(new CurrencyPair(target, baseCurrency), targetToBaseRate);
        }
        
        calculateAllCrossRates(rateCache, baseCurrency);
    }
    
    private static void calculateAllCrossRates(
            Map<CurrencyPair, BigDecimal> rateCache,
            Currency baseCurrency) {
        
        for (Currency source : Currency.values()) {
            if (source == baseCurrency) continue;
            
            for (Currency target : Currency.values()) {
                if (target == baseCurrency || target == source) continue;
                
                CurrencyPair sourceToBasePair = new CurrencyPair(source, baseCurrency);
                CurrencyPair baseToTargetPair = new CurrencyPair(baseCurrency, target);
                
                BigDecimal sourceBaseRate = rateCache.get(sourceToBasePair);
                BigDecimal baseTargetRate = rateCache.get(baseToTargetPair);
                
                BigDecimal crossRate = calculateCrossRate(sourceBaseRate, baseTargetRate);
                
                rateCache.put(new CurrencyPair(source, target), crossRate);
            }
        }
    }
} 