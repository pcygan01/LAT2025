package com.example.LAT2025.dto;

import com.example.LAT2025.model.Currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateFundraisingEventDTO {
    
    @NotBlank(message = "Event name is required")
    private String name;
    
    @NotNull(message = "Account currency is required")
    private Currency accountCurrency;
    
    public CreateFundraisingEventDTO() {
    }
    
    public CreateFundraisingEventDTO(String name, Currency accountCurrency) {
        this.name = name;
        this.accountCurrency = accountCurrency;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Currency getAccountCurrency() {
        return accountCurrency;
    }
    
    public void setAccountCurrency(Currency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
} 