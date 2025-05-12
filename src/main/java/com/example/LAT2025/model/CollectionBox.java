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
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

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
    }

    public boolean isEmpty() {
        return moneyContents.isEmpty() || moneyContents.stream()
                .allMatch(money -> money.getAmount().compareTo(java.math.BigDecimal.ZERO) == 0);
    }

    public boolean isAssigned() {
        return assignedEvent != null;
    }

    public void addMoney(Money money) {
        boolean added = false;
        for (Money existingMoney : moneyContents) {
            if (existingMoney.getCurrency() == money.getCurrency()) {
                existingMoney.setAmount(existingMoney.getAmount().add(money.getAmount()));
                added = true;
                break;
            }
        }
        
        if (!added) {
            moneyContents.add(money);
        }
    }

    public List<Money> emptyBox() {
        List<Money> collectedMoney = new ArrayList<>(moneyContents);
        moneyContents.clear();
        return collectedMoney;
    }
} 