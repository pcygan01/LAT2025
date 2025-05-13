package com.example.LAT2025.service;

import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.Money;

import java.util.List;

public interface CollectionBoxService {
    
    /**
     * Register a new collection box
     * @param identifier Unique identifier for the collection box
     * @return Created collection box
     */
    CollectionBox registerCollectionBox(String identifier);
    
    /**
     * Get a collection box by ID
     * @param id ID of the collection box
     * @return Collection box if found
     */
    CollectionBox getCollectionBoxById(Long id);
    
    /**
     * Get all collection boxes
     * @return List of all collection boxes
     */
    List<CollectionBox> getAllCollectionBoxes();
    
    /**
     * Unregister (remove) a collection box
     * @param id ID of the collection box to unregister
     */
    void unregisterCollectionBox(Long id);
    
    /**
     * Assign a collection box to a fundraising event
     * @param collectionBoxId ID of the collection box
     * @param fundraisingEventId ID of the fundraising event
     * @return Updated collection box
     */
    CollectionBox assignToFundraisingEvent(Long collectionBoxId, Long fundraisingEventId);
    
    /**
     * Add money to a collection box
     * @param collectionBoxId ID of the collection box
     * @param money Money to add
     * @return Updated collection box
     */
    CollectionBox addMoneyToCollectionBox(Long collectionBoxId, Money money);
    
    /**
     * Empty a collection box and transfer money to the assigned fundraising event
     * @param collectionBoxId ID of the collection box
     * @return Updated collection box
     */
    CollectionBox emptyCollectionBox(Long collectionBoxId);
} 