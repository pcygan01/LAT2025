package com.example.LAT2025.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinancialReportDTO {
    
    private List<EventReportEntry> events = new ArrayList<>();
    
    public FinancialReportDTO() {
    }
    
    public List<EventReportEntry> getEvents() {
        return events;
    }
    
    public void setEvents(List<EventReportEntry> events) {
        this.events = events;
    }
    
    public void addEventEntry(String eventName, BigDecimal amount, String currency) {
        events.add(new EventReportEntry(eventName, amount, currency));
    }
    
    public static class EventReportEntry {
        private String eventName;
        private BigDecimal amount;
        private String currency;
        
        public EventReportEntry() {
        }
        
        public EventReportEntry(String eventName, BigDecimal amount, String currency) {
            this.eventName = eventName;
            this.amount = amount;
            this.currency = currency;
        }
        
        public String getEventName() {
            return eventName;
        }
        
        public void setEventName(String eventName) {
            this.eventName = eventName;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getCurrency() {
            return currency;
        }
        
        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
} 