package com.example.LAT2025.service;

import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.Money;

import java.util.List;

public interface CollectionBoxService {
    
    CollectionBox registerCollectionBox(String identifier);
    
    CollectionBox getCollectionBoxById(Long id);
    
    List<CollectionBox> getAllCollectionBoxes();
    
    void unregisterCollectionBox(Long id);
    
    CollectionBox assignToFundraisingEvent(Long collectionBoxId, Long fundraisingEventId);
    
    CollectionBox addMoneyToCollectionBox(Long collectionBoxId, Money money);
    
    CollectionBox emptyCollectionBox(Long collectionBoxId);
    
    CollectionBoxEmptyResult checkAndEmptyCollectionBox(Long collectionBoxId);

    class CollectionBoxEmptyResult {
        private final CollectionBox collectionBox;
        private final boolean wasAlreadyEmpty;
        private final String message;
        
        public CollectionBoxEmptyResult(CollectionBox collectionBox, boolean wasAlreadyEmpty, String message) {
            this.collectionBox = collectionBox;
            this.wasAlreadyEmpty = wasAlreadyEmpty;
            this.message = message;
        }
        
        public CollectionBox getCollectionBox() {
            return collectionBox;
        }
        
        public boolean wasAlreadyEmpty() {
            return wasAlreadyEmpty;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 