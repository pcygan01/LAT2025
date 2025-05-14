package com.example.LAT2025.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_event_id")
    private FundraisingEvent assignedEvent;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "collection_box_id")
    private List<Money> moneyContents = new ArrayList<>();
    
    @Transient
    private Map<Currency, Money> moneyMap;

    public CollectionBox() {
    }

    public CollectionBox(String identifier) {
        this.identifier = identifier;
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

    public FundraisingEvent getAssignedEvent() {
        return assignedEvent;
    }

    public void setAssignedEvent(FundraisingEvent assignedEvent) {
        this.assignedEvent = assignedEvent;
    }

    public List<Money> getMoneyContents() {
        return moneyContents;
    }

    public void setMoneyContents(List<Money> moneyContents) {
        this.moneyContents = moneyContents;
        this.moneyMap = null; // Invalidate cache
    }

    private Map<Currency, Money> getMoneyMap() {
        if (moneyMap == null) {
            moneyMap = new EnumMap<>(Currency.class);
            for (Money money : moneyContents) {
                moneyMap.put(money.getCurrency(), money);
            }
        }
        return moneyMap;
    }

    public boolean isEmpty() {
        return moneyContents.isEmpty() || moneyContents.stream()
                .allMatch(money -> money.getAmount().compareTo(BigDecimal.ZERO) == 0);
    }

    public boolean isAssigned() {
        return assignedEvent != null;
    }

    public void addMoney(Money money) {
        Map<Currency, Money> map = getMoneyMap();
        Currency currency = money.getCurrency();
        
        if (map.containsKey(currency)) {
            // Update existing money
            Money existingMoney = map.get(currency);
            existingMoney.setAmount(existingMoney.getAmount().add(money.getAmount()));
        } else {
            // Add new money to both list and map
            Money newMoney = new Money(money.getAmount(), currency);
            moneyContents.add(newMoney);
            map.put(currency, newMoney);
        }
    }

    public List<Money> emptyBox() {
        List<Money> collectedMoney = new ArrayList<>(moneyContents);
        moneyContents.clear();
        moneyMap = null; // Invalidate cache
        return collectedMoney;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionBox that = (CollectionBox) o;
        return Objects.equals(id, that.id) && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identifier);
    }

    @Override
    public String toString() {
        return "CollectionBox{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", assigned=" + isAssigned() +
                ", empty=" + isEmpty() +
                '}';
    }
} 