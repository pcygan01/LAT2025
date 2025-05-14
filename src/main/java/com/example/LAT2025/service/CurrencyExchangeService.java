package com.example.LAT2025.service;

import com.example.LAT2025.model.Currency;

import java.math.BigDecimal;

public interface CurrencyExchangeService {
    
    BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency);
    
    BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency);

    void refreshRates();
} 