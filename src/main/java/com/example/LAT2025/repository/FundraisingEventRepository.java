package com.example.LAT2025.repository;

import com.example.LAT2025.model.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    Optional<FundraisingEvent> findByName(String name);
    boolean existsByName(String name);
} 