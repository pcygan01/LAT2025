package com.example.LAT2025.model;

import com.example.LAT2025.service.CurrencyExchangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoneyTest {

    @Mock
    private CurrencyExchangeService exchangeService;

    @Test
    void testMoneyCreation() {
        Money money = new Money(new BigDecimal("100.00"), Currency.EUR);
        
        assertEquals(new BigDecimal("100.00"), money.getAmount());
        assertEquals(Currency.EUR, money.getCurrency());
    }
    
    @Test
    void testMoneyEquality() {
        Money money1 = new Money(new BigDecimal("100.00"), Currency.EUR);
        Money money2 = new Money(new BigDecimal("100.00"), Currency.EUR);
        Money money3 = new Money(new BigDecimal("200.00"), Currency.EUR);
        Money money4 = new Money(new BigDecimal("100.00"), Currency.USD);
        
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
        assertNotEquals(money1, money4);
    }
    
    @Test
    void testAddSameCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"), Currency.EUR);
        Money money2 = new Money(new BigDecimal("50.00"), Currency.EUR);
        
        Money result = money1.add(money2, exchangeService);
        
        assertEquals(new BigDecimal("150.00"), result.getAmount());
        assertEquals(Currency.EUR, result.getCurrency());
        
        // Verify that exchange service was not called
        verifyNoInteractions(exchangeService);
    }
    
    @Test
    void testAddDifferentCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"), Currency.EUR);
        Money money2 = new Money(new BigDecimal("50.00"), Currency.USD);
        
        // Mock exchange service to convert 50 USD to 40 EUR
        when(exchangeService.convert(
                eq(new BigDecimal("50.00")), 
                eq(Currency.USD), 
                eq(Currency.EUR)))
            .thenReturn(new BigDecimal("40.00"));
        
        Money result = money1.add(money2, exchangeService);
        
        assertEquals(new BigDecimal("140.00"), result.getAmount());
        assertEquals(Currency.EUR, result.getCurrency());
        
        // Verify exchange service was called correctly
        verify(exchangeService).convert(
                eq(new BigDecimal("50.00")), 
                eq(Currency.USD), 
                eq(Currency.EUR));
    }
    
    @Test
    void testConvertTo() {
        Money money = new Money(new BigDecimal("100.00"), Currency.EUR);
        
        // Mock exchange service to convert 100 EUR to 400 PLN
        when(exchangeService.convert(
                eq(new BigDecimal("100.00")), 
                eq(Currency.EUR), 
                eq(Currency.PLN)))
            .thenReturn(new BigDecimal("400.00"));
        
        Money result = money.convertTo(Currency.PLN, exchangeService);
        
        assertEquals(new BigDecimal("400.00"), result.getAmount());
        assertEquals(Currency.PLN, result.getCurrency());
        
        // Verify exchange service was called correctly
        verify(exchangeService).convert(
                eq(new BigDecimal("100.00")), 
                eq(Currency.EUR), 
                eq(Currency.PLN));
    }
    
    @Test
    void testConvertToSameCurrency() {
        Money money = new Money(new BigDecimal("100.00"), Currency.EUR);
        
        Money result = money.convertTo(Currency.EUR, exchangeService);
        
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(Currency.EUR, result.getCurrency());
        
        // Verify that exchange service was not called
        verifyNoInteractions(exchangeService);
    }
    
    @Test
    void testToString() {
        Money money = new Money(new BigDecimal("100.00"), Currency.EUR);
        
        assertEquals("100.00 EUR", money.toString());
    }
} 