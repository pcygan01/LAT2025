package com.example.LAT2025.dto;

public class CollectionBoxDTO {
    
    private Long id;
    private String identifier;
    private boolean assigned;
    private boolean empty;

    public CollectionBoxDTO() {
    }

    public CollectionBoxDTO(Long id, String identifier, boolean assigned, boolean empty) {
        this.id = id;
        this.identifier = identifier;
        this.assigned = assigned;
        this.empty = empty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
} 