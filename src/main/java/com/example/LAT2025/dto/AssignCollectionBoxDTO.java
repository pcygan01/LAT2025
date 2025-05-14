package com.example.LAT2025.dto;

import jakarta.validation.constraints.NotNull;

public class AssignCollectionBoxDTO {
    
    @NotNull(message = "Fundraising event ID is required")
    private Long fundraisingEventId;
    
    public AssignCollectionBoxDTO() {
    }
    
    public AssignCollectionBoxDTO(Long fundraisingEventId) {
        this.fundraisingEventId = fundraisingEventId;
    }
    
    public Long getFundraisingEventId() {
        return fundraisingEventId;
    }
    
    public void setFundraisingEventId(Long fundraisingEventId) {
        this.fundraisingEventId = fundraisingEventId;
    }
} 