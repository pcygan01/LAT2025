package com.example.LAT2025.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class FundraisingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal accountBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency accountCurrency;

    // Default constructor for JPA
    public FundraisingEvent() {
        this.accountBalance = BigDecimal.ZERO;
    }

    public FundraisingEvent(String name, Currency accountCurrency) {
        this.name = name;
        this.accountCurrency = accountCurrency;
        this.accountBalance = BigDecimal.ZERO;
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

    public void addFunds(Money money) {
        if (money.getCurrency() == this.accountCurrency) {
            this.accountBalance = this.accountBalance.add(money.getAmount());
        } else {
            // Convert the money to the account currency
            BigDecimal convertedAmount = money.getCurrency().convertTo(money.getAmount(), this.accountCurrency);
            this.accountBalance = this.accountBalance.add(convertedAmount);
        }
    }
} 