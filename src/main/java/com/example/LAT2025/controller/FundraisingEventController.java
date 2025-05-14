package com.example.LAT2025.controller;

import com.example.LAT2025.dto.CreateFundraisingEventDTO;
import com.example.LAT2025.dto.FinancialReportDTO;
import com.example.LAT2025.dto.FundraisingEventDTO;
import com.example.LAT2025.model.FundraisingEvent;
import com.example.LAT2025.service.FundraisingEventService;
import com.example.LAT2025.util.DTOConverter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fundraising-events")
@Tag(name = "Fundraising Events", description = "Manage charity fundraising events")
public class FundraisingEventController {
    
    private final FundraisingEventService fundraisingEventService;
    
    @Autowired
    public FundraisingEventController(FundraisingEventService fundraisingEventService) {
        this.fundraisingEventService = fundraisingEventService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new fundraising event",
        description = "Creates a new fundraising event with the specified name and account currency",
        responses = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    public ResponseEntity<FundraisingEventDTO> createFundraisingEvent(
            @Valid @RequestBody CreateFundraisingEventDTO eventDTO) {
        FundraisingEvent event = fundraisingEventService.createFundraisingEvent(
                eventDTO.getName(), 
                eventDTO.getAccountCurrency()
        );
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(DTOConverter.convertToDTO(event));
    }

    @GetMapping
    @Operation(
        summary = "Get all fundraising events",
        description = "Returns a list of all fundraising events",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
        }
    )
    public ResponseEntity<List<FundraisingEventDTO>> getAllFundraisingEvents() {
        List<FundraisingEvent> events = fundraisingEventService.getAllFundraisingEvents();
        List<FundraisingEventDTO> eventDTOs = events.stream()
                .map(DTOConverter::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get a fundraising event by ID",
        description = "Returns a single fundraising event by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Event not found")
        }
    )
    public ResponseEntity<FundraisingEventDTO> getFundraisingEventById(
            @Parameter(description = "ID of the fundraising event") @PathVariable Long id) {
        FundraisingEvent event = fundraisingEventService.getFundraisingEventById(id);
        return ResponseEntity.ok(DTOConverter.convertToDTO(event));
    }

    @GetMapping("/financial-report")
    @Operation(
        summary = "Generate financial report",
        description = "Generates a financial report with all fundraising events and their account balances",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
        }
    )
    public ResponseEntity<FinancialReportDTO> getFinancialReport() {
        List<FundraisingEvent> events = fundraisingEventService.getAllFundraisingEvents();
        FinancialReportDTO report = new FinancialReportDTO();
        
        for (FundraisingEvent event : events) {
            report.addEventEntry(
                    event.getName(),
                    event.getAccountBalance(),
                    event.getAccountCurrency().name()
            );
        }
        
        return ResponseEntity.ok(report);
    }
} 