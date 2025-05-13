package com.example.LAT2025.repository;

import com.example.LAT2025.model.CollectionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
    Optional<CollectionBox> findByIdentifier(String identifier);
    boolean existsByIdentifier(String identifier);
} 