package com.example.LAT2025.service;

import com.example.LAT2025.exception.BusinessRuleViolationException;
import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;
import com.example.LAT2025.repository.FundraisingEventRepository;
import com.example.LAT2025.service.impl.FundraisingEventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FundraisingEventServiceTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;
    
    @Mock
    private CurrencyExchangeService currencyExchangeService;
    
    private FundraisingEventService fundraisingEventService;
    
    @BeforeEach
    void setUp() {
        fundraisingEventService = new FundraisingEventServiceImpl(fundraisingEventRepository, currencyExchangeService);
    }
    
    @Test
    void testCreateFundraisingEvent() {
        String eventName = "Test Event";
        Currency currency = Currency.EUR;
        
        when(fundraisingEventRepository.existsByName(eventName)).thenReturn(false);
        
        FundraisingEvent savedEvent = new FundraisingEvent(eventName, currency);
        savedEvent.setId(1L);
        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);
        
        FundraisingEvent result = fundraisingEventService.createFundraisingEvent(eventName, currency);
        
        assertNotNull(result);
        assertEquals(eventName, result.getName());
        assertEquals(currency, result.getAccountCurrency());
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.ZERO, result.getAccountBalance());
        
        verify(fundraisingEventRepository).existsByName(eventName);
        verify(fundraisingEventRepository).save(any(FundraisingEvent.class));
    }
    
    @Test
    void testCreateFundraisingEventWithDuplicateName() {
        String eventName = "Test Event";
        Currency currency = Currency.EUR;
        
        when(fundraisingEventRepository.existsByName(eventName)).thenReturn(true);
        
        assertThrows(BusinessRuleViolationException.class, () -> {
            fundraisingEventService.createFundraisingEvent(eventName, currency);
        });
        
        verify(fundraisingEventRepository).existsByName(eventName);
        verify(fundraisingEventRepository, never()).save(any(FundraisingEvent.class));
    }
    
    @Test
    void testGetFundraisingEventById() {
        Long eventId = 1L;
        FundraisingEvent event = new FundraisingEvent("Test Event", Currency.EUR);
        event.setId(eventId);
        
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        
        FundraisingEvent result = fundraisingEventService.getFundraisingEventById(eventId);
        
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Test Event", result.getName());
        
        verify(fundraisingEventRepository).findById(eventId);
    }
    
    @Test
    void testGetFundraisingEventByIdNotFound() {
        Long eventId = 1L;
        
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.empty());
        
        assertThrows(BusinessRuleViolationException.class, () -> {
            fundraisingEventService.getFundraisingEventById(eventId);
        });
        
        verify(fundraisingEventRepository).findById(eventId);
    }
    
    @Test
    void testGetAllFundraisingEvents() {
        FundraisingEvent event1 = new FundraisingEvent("Event 1", Currency.EUR);
        event1.setId(1L);
        FundraisingEvent event2 = new FundraisingEvent("Event 2", Currency.PLN);
        event2.setId(2L);
        
        List<FundraisingEvent> events = Arrays.asList(event1, event2);
        
        when(fundraisingEventRepository.findAll()).thenReturn(events);
        
        List<FundraisingEvent> result = fundraisingEventService.getAllFundraisingEvents();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getName());
        assertEquals("Event 2", result.get(1).getName());
        
        verify(fundraisingEventRepository).findAll();
    }
    
    @Test
    void testAddFundsToEvent() {
        Long eventId = 1L;
        BigDecimal initialBalance = new BigDecimal("1000.00");
        
        FundraisingEvent event = new FundraisingEvent("Test Event", Currency.EUR);
        event.setId(eventId);
        event.setAccountBalance(initialBalance);
        
        Money moneyToAdd = new Money(new BigDecimal("200.00"), Currency.USD);
        
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        when(currencyExchangeService.convert(
                eq(new BigDecimal("200.00")), 
                eq(Currency.USD), 
                eq(Currency.EUR)))
            .thenReturn(new BigDecimal("180.00"));
        
        FundraisingEvent result = fundraisingEventService.addFundsToEvent(eventId, moneyToAdd);
        
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        
        // Expected balance: 1000 + 180 = 1180
        assertEquals(0, new BigDecimal("1180.00").compareTo(result.getAccountBalance()));
        
        verify(fundraisingEventRepository).findById(eventId);
        verify(fundraisingEventRepository).save(any(FundraisingEvent.class));
        
        verify(currencyExchangeService).convert(
                any(BigDecimal.class), 
                eq(Currency.USD), 
                eq(Currency.EUR));
    }
    
    @Test
    void testAddFundsToEventWithSameCurrency() {
        Long eventId = 1L;
        BigDecimal initialBalance = new BigDecimal("1000.00");
        
        FundraisingEvent event = new FundraisingEvent("Test Event", Currency.EUR);
        event.setId(eventId);
        event.setAccountBalance(initialBalance);
        
        Money moneyToAdd = new Money(new BigDecimal("200.00"), Currency.EUR);
        
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        FundraisingEvent result = fundraisingEventService.addFundsToEvent(eventId, moneyToAdd);
        
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        
        // Expected balance: 1000 + 200 = 1200
        assertEquals(0, new BigDecimal("1200.00").compareTo(result.getAccountBalance()));
        
        verify(fundraisingEventRepository).findById(eventId);
        verify(fundraisingEventRepository).save(any(FundraisingEvent.class));
        
        verifyNoInteractions(currencyExchangeService);
    }
} 