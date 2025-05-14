package com.example.LAT2025.model;

import com.example.LAT2025.service.CurrencyExchangeService;
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
import java.util.Objects;

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

    public Money add(Money other, CurrencyExchangeService exchangeService) {
        if (this.currency == other.currency) {
            return new Money(this.amount.add(other.amount), this.currency);
        } else {
            // Convert the other currency to this currency and then add
            Money convertedMoney = other.convertTo(this.currency, exchangeService);
            return new Money(this.amount.add(convertedMoney.amount), this.currency);
        }
    }

    public Money convertTo(Currency targetCurrency, CurrencyExchangeService exchangeService) {
        if (this.currency == targetCurrency) {
            return new Money(this.amount, this.currency);
        }
        
        BigDecimal convertedAmount = exchangeService.convert(this.amount, this.currency, targetCurrency);
        return new Money(convertedAmount, targetCurrency);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
} 