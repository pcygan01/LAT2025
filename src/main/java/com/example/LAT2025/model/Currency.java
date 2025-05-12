package com.example.LAT2025.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Currency {
    EUR(BigDecimal.ONE),
    USD(new BigDecimal("0.85")),
    GBP(new BigDecimal("1.15"));

    private final BigDecimal exchangeRateToEUR;

    Currency(BigDecimal exchangeRateToEUR) {
        this.exchangeRateToEUR = exchangeRateToEUR;
    }

    public BigDecimal getExchangeRateToEUR() {
        return exchangeRateToEUR;
    }

    public BigDecimal convertToEUR(BigDecimal amount) {
        return amount.multiply(exchangeRateToEUR);
    }

    public BigDecimal convertFromEUR(BigDecimal amountInEUR) {
        return amountInEUR.divide(exchangeRateToEUR, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal convertTo(BigDecimal amount, Currency targetCurrency) {
        BigDecimal amountInEUR = this.convertToEUR(amount);
        return targetCurrency.convertFromEUR(amountInEUR);
    }
} 