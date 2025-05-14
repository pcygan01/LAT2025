package com.example.LAT2025.service.impl;

import com.example.LAT2025.exception.BusinessRuleViolationException;
import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;
import com.example.LAT2025.repository.FundraisingEventRepository;
import com.example.LAT2025.service.CurrencyExchangeService;
import com.example.LAT2025.service.FundraisingEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FundraisingEventServiceImpl implements FundraisingEventService {

    private final FundraisingEventRepository fundraisingEventRepository;
    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public FundraisingEventServiceImpl(
            FundraisingEventRepository fundraisingEventRepository, 
            CurrencyExchangeService currencyExchangeService) {
        this.fundraisingEventRepository = fundraisingEventRepository;
        this.currencyExchangeService = currencyExchangeService;
    }

    @Override
    @Transactional
    public FundraisingEvent createFundraisingEvent(String name, Currency accountCurrency) {
        if (fundraisingEventRepository.existsByName(name)) {
            throw new BusinessRuleViolationException("Fundraising event with name '" + name + "' already exists");
        }
        
        FundraisingEvent event = new FundraisingEvent(name, accountCurrency);
        return fundraisingEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public FundraisingEvent getFundraisingEventById(Long id) {
        return fundraisingEventRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException("Fundraising event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FundraisingEvent> getAllFundraisingEvents() {
        return fundraisingEventRepository.findAll();
    }

    @Override
    @Transactional
    public FundraisingEvent addFundsToEvent(Long eventId, Money money) {
        FundraisingEvent event = getFundraisingEventById(eventId);
        event.addFunds(money, currencyExchangeService);
        return fundraisingEventRepository.save(event);
    }
} 