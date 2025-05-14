package com.example.LAT2025.dto;

import com.example.LAT2025.model.Currency;

import java.math.BigDecimal;

public class FundraisingEventDTO {
    
    private Long id;
    private String name;
    private BigDecimal accountBalance;
    private Currency accountCurrency;
    
    public FundraisingEventDTO() {
    }
    
    public FundraisingEventDTO(Long id, String name, BigDecimal accountBalance, Currency accountCurrency) {
        this.id = id;
        this.name = name;
        this.accountBalance = accountBalance;
        this.accountCurrency = accountCurrency;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public Currency getAccountCurrency() {
        return accountCurrency;
    }
    
    public void setAccountCurrency(Currency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
} 