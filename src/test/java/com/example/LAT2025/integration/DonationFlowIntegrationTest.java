package com.example.LAT2025.integration;

import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;
import com.example.LAT2025.repository.CollectionBoxRepository;
import com.example.LAT2025.repository.FundraisingEventRepository;
import com.example.LAT2025.service.CollectionBoxService;
import com.example.LAT2025.service.CurrencyExchangeService;
import com.example.LAT2025.service.FundraisingEventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DonationFlowIntegrationTest {
    
    @Autowired
    private FundraisingEventService fundraisingEventService;
    
    @Autowired
    private CollectionBoxService collectionBoxService;
    
    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    
    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;
    
    @Autowired
    private CollectionBoxRepository collectionBoxRepository;
    
    private FundraisingEvent fundraisingEvent;
    private CollectionBox collectionBox;
    
    @BeforeEach
    void setUp() {
        // Clear any existing data
        collectionBoxRepository.deleteAll();
        fundraisingEventRepository.deleteAll();
        
        // Create a fundraising event
        fundraisingEvent = fundraisingEventService.createFundraisingEvent("Charity Marathon", Currency.EUR);
        
        // Create a collection box
        collectionBox = collectionBoxService.registerCollectionBox("BOX-001");
        
        // Assign the box to the event
        collectionBox = collectionBoxService.assignToFundraisingEvent(collectionBox.getId(), fundraisingEvent.getId());
    }
    
    @AfterEach
    void tearDown() {
        collectionBoxRepository.deleteAll();
        fundraisingEventRepository.deleteAll();
    }
    
    @Test
    void testCollectionBoxEmptyWhenAlreadyEmpty() {
        assertTrue(collectionBox.isEmpty());
        assertEquals(BigDecimal.ZERO, fundraisingEvent.getAccountBalance());
        
        collectionBox = collectionBoxService.getCollectionBoxById(collectionBox.getId());
        assertTrue(collectionBox.isEmpty());
        
        fundraisingEvent = fundraisingEventService.getFundraisingEventById(fundraisingEvent.getId());
        assertEquals(BigDecimal.ZERO, fundraisingEvent.getAccountBalance());
    }
    
    @Test
    void testBasicDonationFlow() {
        // Add money to the box
        Money donation = new Money(new BigDecimal("50.00"), Currency.EUR);
        collectionBoxService.addMoneyToCollectionBox(collectionBox.getId(), donation);
        
        // Verify box is not empty
        collectionBox = collectionBoxService.getCollectionBoxById(collectionBox.getId());
        assertFalse(collectionBox.isEmpty());
        
        // Empty the box
        collectionBox = collectionBoxService.emptyCollectionBox(collectionBox.getId());
        
        // Verify box is now empty
        assertTrue(collectionBox.isEmpty());
        
        // Verify the money was transferred to the event
        fundraisingEvent = fundraisingEventService.getFundraisingEventById(fundraisingEvent.getId());
        assertEquals(0, new BigDecimal("50.00").compareTo(fundraisingEvent.getAccountBalance()));
    }
    
    @Test
    void testMultiCurrencyDonation() {
        // Add donations in different currencies
        collectionBoxService.addMoneyToCollectionBox(collectionBox.getId(), new Money(new BigDecimal("20.00"), Currency.EUR));
        collectionBoxService.addMoneyToCollectionBox(collectionBox.getId(), new Money(new BigDecimal("30.00"), Currency.USD));
        
        // Empty the box
        collectionBox = collectionBoxService.emptyCollectionBox(collectionBox.getId());
        
        // Verify box is now empty
        assertTrue(collectionBox.isEmpty());
        
        // Get the expected amount in EUR (the event's currency)
        BigDecimal usdInEur = currencyExchangeService.convert(
                new BigDecimal("30.00"), Currency.USD, Currency.EUR);
        BigDecimal expectedTotal = new BigDecimal("20.00").add(usdInEur);
        
        // Verify the event balance
        fundraisingEvent = fundraisingEventService.getFundraisingEventById(fundraisingEvent.getId());
        
        // Allow small difference due to rounding
        BigDecimal diff = fundraisingEvent.getAccountBalance().subtract(expectedTotal).abs();
        assertTrue(diff.compareTo(new BigDecimal("0.01")) <= 0, 
                "Expected balance close to " + expectedTotal + " but was " + fundraisingEvent.getAccountBalance());
    }
} 