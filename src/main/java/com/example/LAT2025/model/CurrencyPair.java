package com.example.LAT2025.model;

import java.util.Objects;

/**
 * A pair of currencies representing a conversion from source to target currency
 */
public class CurrencyPair {
    private final Currency source;
    private final Currency target;
    
    public CurrencyPair(Currency source, Currency target) {
        this.source = source;
        this.target = target;
    }
    
    public Currency getSource() {
        return source;
    }
    
    public Currency getTarget() {
        return target;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyPair that = (CurrencyPair) o;
        return source == that.source && target == that.target;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
    
    @Override
    public String toString() {
        return source + "_TO_" + target;
    }
} 