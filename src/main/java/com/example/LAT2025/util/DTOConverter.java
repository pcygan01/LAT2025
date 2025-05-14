package com.example.LAT2025.util;

import com.example.LAT2025.dto.CollectionBoxDTO;
import com.example.LAT2025.dto.FundraisingEventDTO;
import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.FundraisingEvent;

import java.util.List;
import java.util.stream.Collectors;

public final class DTOConverter {
    
    private DTOConverter() {
    }
    
    public static FundraisingEventDTO convertToDTO(FundraisingEvent event) {
        return new FundraisingEventDTO(
                event.getId(),
                event.getName(),
                event.getAccountBalance(),
                event.getAccountCurrency()
        );
    }
    
    public static List<FundraisingEventDTO> convertToDTO(List<FundraisingEvent> events) {
        return events.stream()
                .map(DTOConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public static CollectionBoxDTO convertToDTO(CollectionBox box) {
        return new CollectionBoxDTO(
                box.getId(),
                box.getIdentifier(),
                box.isAssigned(),
                box.isEmpty()
        );
    }
    
    public static List<CollectionBoxDTO> convertToCollectionBoxDTO(List<CollectionBox> boxes) {
        return boxes.stream()
                .map(DTOConverter::convertToDTO)
                .collect(Collectors.toList());
    }
} 