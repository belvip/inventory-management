package com.belvinard.inventory_management.controller;

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
}