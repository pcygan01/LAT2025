package com.example.LAT2025.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCollectionBoxDTO {
    
    @NotBlank(message = "Collection box identifier is required")
    private String identifier;
    
    public CreateCollectionBoxDTO() {
    }
    
    public CreateCollectionBoxDTO(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
} 