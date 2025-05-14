package com.example.LAT2025.service;

import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;

import java.util.List;

public interface FundraisingEventService {
    
    FundraisingEvent createFundraisingEvent(String name, Currency accountCurrency);
    
    FundraisingEvent getFundraisingEventById(Long id);

    List<FundraisingEvent> getAllFundraisingEvents();
    
    FundraisingEvent addFundsToEvent(Long eventId, Money money);
} 