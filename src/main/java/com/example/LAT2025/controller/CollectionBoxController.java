package com.example.LAT2025.controller;

import com.example.LAT2025.dto.AddMoneyDTO;
import com.example.LAT2025.dto.AssignCollectionBoxDTO;
import com.example.LAT2025.dto.CollectionBoxDTO;
import com.example.LAT2025.dto.CreateCollectionBoxDTO;
import com.example.LAT2025.model.CollectionBox;
import com.example.LAT2025.model.Money;
import com.example.LAT2025.service.CollectionBoxService;
import com.example.LAT2025.service.CollectionBoxService.CollectionBoxEmptyResult;
import com.example.LAT2025.util.DTOConverter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collection-boxes")
@Tag(name = "Collection Boxes", description = "Manage charity collection boxes")
public class CollectionBoxController {
    
    private final CollectionBoxService collectionBoxService;
    
    @Autowired
    public CollectionBoxController(CollectionBoxService collectionBoxService) {
        this.collectionBoxService = collectionBoxService;
    }

    @PostMapping
    @Operation(
        summary = "Register a new collection box",
        description = "Registers a new collection box with the specified identifier",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Collection box registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    public ResponseEntity<CollectionBoxDTO> registerCollectionBox(
            @Valid @RequestBody CreateCollectionBoxDTO boxDTO) {
        CollectionBox box = collectionBoxService.registerCollectionBox(boxDTO.getIdentifier());
        CollectionBoxDTO dto = DTOConverter.convertToDTO(box);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(dto);
    }

    @GetMapping
    @Operation(
        summary = "List all collection boxes",
        description = "Returns a list of all collection boxes with limited information (no actual monetary value shown)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful operation")
        }
    )
    public ResponseEntity<List<CollectionBoxDTO>> getAllCollectionBoxes() {
        List<CollectionBox> boxes = collectionBoxService.getAllCollectionBoxes();
        List<CollectionBoxDTO> boxDTOs = boxes.stream()
                .map(DTOConverter::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(boxDTOs);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get a collection box by ID",
        description = "Returns a single collection box by its ID",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful operation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Collection box not found")
        }
    )
    public ResponseEntity<CollectionBoxDTO> getCollectionBoxById(
            @Parameter(description = "ID of the collection box") @PathVariable Long id) {
        CollectionBox box = collectionBoxService.getCollectionBoxById(id);
        return ResponseEntity.ok(DTOConverter.convertToDTO(box));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Unregister a collection box",
        description = "Unregisters (removes) a collection box by its ID. The box is automatically emptied.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Collection box unregistered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Collection box not found")
        }
    )
    public ResponseEntity<Void> unregisterCollectionBox(
            @Parameter(description = "ID of the collection box") @PathVariable Long id) {
        collectionBoxService.unregisterCollectionBox(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    @Operation(
        summary = "Assign a collection box to a fundraising event",
        description = "Assigns a collection box to an existing fundraising event. The box must be empty.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Collection box assigned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Box is not empty or invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Collection box or fundraising event not found")
        }
    )
    public ResponseEntity<CollectionBoxDTO> assignToFundraisingEvent(
            @Parameter(description = "ID of the collection box") @PathVariable Long id,
            @Valid @RequestBody AssignCollectionBoxDTO assignDTO) {
        CollectionBox box = collectionBoxService.assignToFundraisingEvent(id, assignDTO.getFundraisingEventId());
        return ResponseEntity.ok(DTOConverter.convertToDTO(box));
    }

    @PostMapping("/{id}/add-money")
    @Operation(
        summary = "Add money to a collection box",
        description = "Adds money to a collection box in the specified currency",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Money added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Collection box not found")
        }
    )
    public ResponseEntity<CollectionBoxDTO> addMoneyToCollectionBox(
            @Parameter(description = "ID of the collection box") @PathVariable Long id,
            @Valid @RequestBody AddMoneyDTO moneyDTO) {
        Money money = new Money(moneyDTO.getAmount(), moneyDTO.getCurrency());
        CollectionBox box = collectionBoxService.addMoneyToCollectionBox(id, money);
        return ResponseEntity.ok(DTOConverter.convertToDTO(box));
    }

    @PostMapping("/{id}/empty")
    @Operation(
        summary = "Empty a collection box",
        description = "Empties a collection box and transfers money to the assigned fundraising event's account",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Collection box emptied successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Box is not assigned to any event"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Collection box not found")
        }
    )
    public ResponseEntity<CollectionBoxDTO> emptyCollectionBox(
            @Parameter(description = "ID of the collection box") @PathVariable Long id) {
        
        CollectionBoxEmptyResult result = collectionBoxService.checkAndEmptyCollectionBox(id);
        CollectionBoxDTO boxDTO = DTOConverter.convertToDTO(result.getCollectionBox());
        return ResponseEntity.ok(boxDTO);
    }
} 