package com.example.LAT2025.service;

import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;

import java.util.List;

public interface FundraisingEventService {
    
    /**
     * Create a new fundraising event
     * @param name Name of the fundraising event
     * @param accountCurrency Currency for the event's account
     * @return Created fundraising event
     */
    FundraisingEvent createFundraisingEvent(String name, Currency accountCurrency);
    
    /**
     * Get a fundraising event by ID
     * @param id ID of the fundraising event
     * @return Fundraising event if found
     */
    FundraisingEvent getFundraisingEventById(Long id);
    
    /**
     * Get all fundraising events
     * @return List of all fundraising events
     */
    List<FundraisingEvent> getAllFundraisingEvents();
    
    /**
     * Add money to a fundraising event's account
     * @param eventId ID of the fundraising event
     * @param money Money to add
     * @return Updated fundraising event
     */
    FundraisingEvent addFundsToEvent(Long eventId, Money money);
} 