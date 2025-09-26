package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.CreateClientOrderMovementDto;
import com.belvinard.inventory_management.dto.request.CreateManualAdjustmentDto;
import com.belvinard.inventory_management.dto.request.CreateSupplierOrderMovementDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.dto.response.StockMovementResponseDto;
import com.belvinard.inventory_management.model.MvtOrigin;
import com.belvinard.inventory_management.model.StockMovementType;
import com.belvinard.inventory_management.service.StockMovementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
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
            @Valid @RequestBody CreateSupplierOrderMovementDto dto) {
        StockMovementResponseDto movement = stockMovementService.createMovementForSupplierOrder(
                dto.articleId(), dto.quantity(), dto.supplierOrderId(), dto.description());
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
            @Valid @RequestBody CreateClientOrderMovementDto dto) {
        StockMovementResponseDto movement = stockMovementService.createMovementForClientOrder(
                dto.articleId(), dto.quantity(), dto.clientOrderId(), dto.description());
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
            @Valid @RequestBody CreateManualAdjustmentDto dto) {
        StockMovementResponseDto movement = stockMovementService.createManualAdjustment(
                dto.articleId(), dto.quantity(), dto.type(), dto.description());
        return ResponseEntity.status(201).body(movement);
    }

    @Operation(summary = "Get all IN movements", description = "Retrieves all stock IN movements with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "IN movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/in")
    public ResponseEntity<PagedResponse<StockMovementResponseDto>> getAllInMovements(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        PagedResponse<StockMovementResponseDto> movements = stockMovementService.getAllInMovements(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get all OUT movements", description = "Retrieves all stock OUT movements with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OUT movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/out")
    public ResponseEntity<PagedResponse<StockMovementResponseDto>> getAllOutMovements(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        PagedResponse<StockMovementResponseDto> movements = stockMovementService.getAllOutMovements(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get IN movements by article", description = "Retrieves all IN movements for a specific article")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article IN movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/article/{articleId}/in")
    public ResponseEntity<java.util.List<StockMovementResponseDto>> getInMovementsByArticle(@PathVariable Long articleId) {
        java.util.List<StockMovementResponseDto> movements = stockMovementService.getInMovementsByArticle(articleId);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get OUT movements by article", description = "Retrieves all OUT movements for a specific article")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article OUT movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/article/{articleId}/out")
    public ResponseEntity<java.util.List<StockMovementResponseDto>> getOutMovementsByArticle(@PathVariable Long articleId) {
        java.util.List<StockMovementResponseDto> movements = stockMovementService.getOutMovementsByArticle(articleId);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get all movements by article", description = "Retrieves all movements (IN and OUT) for a specific article")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<java.util.List<StockMovementResponseDto>> getMovementsByArticle(@PathVariable Long articleId) {
        java.util.List<StockMovementResponseDto> movements = stockMovementService.getMovementsByArticle(articleId);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get movements by type", description = "Retrieves movements filtered by type (IN/OUT) with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movements by type retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<StockMovementResponseDto>> getMovementsByType(
            @PathVariable StockMovementType type,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<StockMovementResponseDto> movements = stockMovementService.getMovementsByType(type, pageable);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get movements by origin", description = "Retrieves movements filtered by origin with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movements by origin retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/origin/{origin}")
    public ResponseEntity<Page<StockMovementResponseDto>> getMovementsByOrigin(
            @PathVariable MvtOrigin origin,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<StockMovementResponseDto> movements = stockMovementService.getMovementsByOrigin(origin, pageable);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get total IN movements for article", description = "Returns the total quantity of IN movements for a specific article")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total IN movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/article/{articleId}/total-in")
    public ResponseEntity<Long> getTotalInMovements(@PathVariable Long articleId) {
        Long total = stockMovementService.getTotalInMovements(articleId);
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Get total OUT movements for article", description = "Returns the total quantity of OUT movements for a specific article")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total OUT movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/article/{articleId}/total-out")
    public ResponseEntity<Long> getTotalOutMovements(@PathVariable Long articleId) {
        Long total = stockMovementService.getTotalOutMovements(articleId);
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Get recent movements", description = "Returns the most recent stock movements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent movements retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/recent")
    public ResponseEntity<java.util.List<StockMovementResponseDto>> getRecentMovements(
            @RequestParam(defaultValue = "10") int limit) {
        java.util.List<StockMovementResponseDto> movements = stockMovementService.getRecentMovements(limit);
        return ResponseEntity.ok(movements);
    }
}