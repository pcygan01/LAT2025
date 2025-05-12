package com.example.LAT2025.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Money {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public Money() {
    }

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Money add(Money other) {
        if (this.currency == other.currency) {
            return new Money(this.amount.add(other.amount), this.currency);
        } else {
            BigDecimal convertedAmount = other.currency.convertTo(other.amount, this.currency);
            return new Money(this.amount.add(convertedAmount), this.currency);
        }
    }

    public Money convertTo(Currency targetCurrency) {
        if (this.currency == targetCurrency) {
            return new Money(this.amount, this.currency);
        }
        
        BigDecimal convertedAmount = this.currency.convertTo(this.amount, targetCurrency);
        return new Money(convertedAmount, targetCurrency);
    }
} 