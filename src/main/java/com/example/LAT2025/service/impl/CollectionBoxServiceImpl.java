package com.example.LAT2025.service.impl;

import com.example.LAT2025.exception.BusinessRuleViolationException;
import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.model.Money;
import com.example.LAT2025.repository.CollectionBoxRepository;
import com.example.LAT2025.service.CollectionBoxService;
import com.example.LAT2025.service.FundraisingEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollectionBoxServiceImpl implements CollectionBoxService {

    private final CollectionBoxRepository collectionBoxRepository;
    private final FundraisingEventService fundraisingEventService;

    @Autowired
    public CollectionBoxServiceImpl(CollectionBoxRepository collectionBoxRepository,
                                   FundraisingEventService fundraisingEventService) {
        this.collectionBoxRepository = collectionBoxRepository;
        this.fundraisingEventService = fundraisingEventService;
    }

    @Override
    @Transactional
    public CollectionBox registerCollectionBox(String identifier) {
        if (collectionBoxRepository.existsByIdentifier(identifier)) {
            throw new BusinessRuleViolationException("Collection box with identifier '" + identifier + "' already exists");
        }
        
        CollectionBox collectionBox = new CollectionBox(identifier);
        return collectionBoxRepository.save(collectionBox);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionBox getCollectionBoxById(Long id) {
        return collectionBoxRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException("Collection box not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionBox> getAllCollectionBoxes() {
        return collectionBoxRepository.findAll();
    }

    @Override
    @Transactional
    public void unregisterCollectionBox(Long id) {
        CollectionBox collectionBox = getCollectionBoxById(id);
        
        // When a box is unregistered, it's automatically emptied, but money is not transferred
        collectionBox.emptyBox();
        
        collectionBoxRepository.delete(collectionBox);
    }

    @Override
    @Transactional
    public CollectionBox assignToFundraisingEvent(Long collectionBoxId, Long fundraisingEventId) {
        CollectionBox collectionBox = getCollectionBoxById(collectionBoxId);
        FundraisingEvent fundraisingEvent = fundraisingEventService.getFundraisingEventById(fundraisingEventId);
        
        // Check if the box is empty before assigning
        if (!collectionBox.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot assign a non-empty collection box to a fundraising event");
        }
        
        collectionBox.setAssignedEvent(fundraisingEvent);
        return collectionBoxRepository.save(collectionBox);
    }

    @Override
    @Transactional
    public CollectionBox addMoneyToCollectionBox(Long collectionBoxId, Money money) {
        CollectionBox collectionBox = getCollectionBoxById(collectionBoxId);
        collectionBox.addMoney(money);
        return collectionBoxRepository.save(collectionBox);
    }

    @Override
    @Transactional
    public CollectionBox emptyCollectionBox(Long collectionBoxId) {
        CollectionBox collectionBox = getCollectionBoxById(collectionBoxId);
        
        // Check if the box is assigned to an event
        if (!collectionBox.isAssigned()) {
            throw new BusinessRuleViolationException("Cannot empty a collection box that is not assigned to a fundraising event");
        }
        
        // Transfer money to the fundraising event
        FundraisingEvent fundraisingEvent = collectionBox.getAssignedEvent();
        List<Money> moneyContents = collectionBox.emptyBox();
        
        for (Money money : moneyContents) {
            fundraisingEvent.addFunds(money);
        }
        
        fundraisingEventService.addFundsToEvent(fundraisingEvent.getId(), new Money(java.math.BigDecimal.ZERO, fundraisingEvent.getAccountCurrency()));
        
        return collectionBoxRepository.save(collectionBox);
    }
} 