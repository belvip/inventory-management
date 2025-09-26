package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.model.StockMovementType;
import com.belvinard.inventory_management.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/stock-movements")
@RequiredArgsConstructor
@Tag(name = "Stock Movements", description = "Stock movement management operations")
public class StockMovementController {
    
    private final StockMovementService stockMovementService;

    @Operation(summary = "Create stock movements for supplier order", description = "Creates stock movements when supplier order is completed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movements created successfully"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/supplier-order/{supplierOrderId}")
    public ResponseEntity<Void> createMovementsForSupplierOrder(@PathVariable Long supplierOrderId) {
        stockMovementService.createStockMovementForOrder(supplierOrderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create stock movements for client order", description = "Creates stock movements when client order is completed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movements created successfully"),
        @ApiResponse(responseCode = "404", description = "Client order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/client-order/{clientOrderId}")
    public ResponseEntity<Void> createMovementsForClientOrder(@PathVariable Long clientOrderId) {
        stockMovementService.createStockMovementClientOrder(clientOrderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Create stock movements for sale", description = "Creates stock movements when sale is finalized")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movements created successfully"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/sale/{saleId}")
    public ResponseEntity<Void> createMovementsForSale(@PathVariable Long saleId) {
        stockMovementService.createStockMovementForSale(saleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get stock movement by ID", description = "Retrieves a stock movement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movement found"),
        @ApiResponse(responseCode = "404", description = "Stock movement not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponseDto> getById(@PathVariable Long id) {
        StockMovementResponseDto movement = stockMovementService.getById(id);
        return ResponseEntity.ok(movement);
    }

    @Operation(summary = "Get all stock movements", description = "Retrieves all stock movements with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<StockMovementResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        PagedResponse<StockMovementResponseDto> movements = stockMovementService.getAll(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Create movement for supplier order", description = "Creates a specific stock movement for supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movement created successfully"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create/supplier-order")
    public ResponseEntity<StockMovementResponseDto> createMovementForSupplierOrder(
            @RequestParam Long articleId,
            @RequestParam Long quantity,
            @RequestParam Long supplierOrderId,
            @RequestParam String description) {
        StockMovementResponseDto movement = stockMovementService.createMovementForSupplierOrder(articleId, quantity, supplierOrderId, description);
        return ResponseEntity.status(201).body(movement);
    }

    @Operation(summary = "Create movement for client order", description = "Creates a specific stock movement for client order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movement created successfully"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create/client-order")
    public ResponseEntity<StockMovementResponseDto> createMovementForClientOrder(
            @RequestParam Long articleId,
            @RequestParam Long quantity,
            @RequestParam Long clientOrderId,
            @RequestParam String description) {
        StockMovementResponseDto movement = stockMovementService.createMovementForClientOrder(articleId, quantity, clientOrderId, description);
        return ResponseEntity.status(201).body(movement);
    }

    @Operation(summary = "Create manual adjustment", description = "Creates a manual stock adjustment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Adjustment created successfully"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create/manual-adjustment")
    public ResponseEntity<StockMovementResponseDto> createManualAdjustment(
            @RequestParam Long articleId,
            @RequestParam Long quantity,
            @RequestParam StockMovementType type,
            @RequestParam String description) {
        StockMovementResponseDto movement = stockMovementService.createManualAdjustment(articleId, quantity, type, description);
        return ResponseEntity.status(201).body(movement);
    }
}