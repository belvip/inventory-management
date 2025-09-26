package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.SupplierOrderLineRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderLineResponseDto;
import com.belvinard.inventory_management.service.SupplierOrderLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/supplier-order-lines")
@RequiredArgsConstructor
@Tag(name = "Supplier Order Lines", description = "Supplier order line management operations")
public class SupplierOrderLineController {
    
    private final SupplierOrderLineService supplierOrderLineService;

    @Operation(summary = "Add line to supplier order", description = "Adds a new line to a supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Line added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or order confirmed"),
        @ApiResponse(responseCode = "404", description = "Order or article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<SupplierOrderLineResponseDto> addLineToOrder(
            @Valid @RequestBody SupplierOrderLineRequestDto dto) {
        SupplierOrderLineResponseDto createdLine = supplierOrderLineService.addLineToOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLine);
    }

    @Operation(summary = "Get supplier order line by ID", description = "Retrieves a supplier order line by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Line found"),
        @ApiResponse(responseCode = "404", description = "Line not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrderLineResponseDto> getLineById(@PathVariable Long id) {
        SupplierOrderLineResponseDto line = supplierOrderLineService.getLineById(id);
        return ResponseEntity.ok(line);
    }

    @Operation(summary = "Update supplier order line", description = "Updates a supplier order line (article and quantity)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Line updated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot update - order not in PENDING status"),
        @ApiResponse(responseCode = "404", description = "Line not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierOrderLineResponseDto> updateLine(
            @PathVariable Long id, @Valid @RequestBody SupplierOrderLineRequestDto dto) {
        SupplierOrderLineResponseDto updatedLine = supplierOrderLineService.update(id, dto);
        return ResponseEntity.ok(updatedLine);
    }

    @Operation(summary = "Get all lines for supplier order", description = "Retrieves all lines for a specific supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lines retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/order/{supplierOrderId}")
    public ResponseEntity<List<SupplierOrderLineResponseDto>> getAllLinesForOrder(@PathVariable Long supplierOrderId) {
        List<SupplierOrderLineResponseDto> lines = supplierOrderLineService.getAllLinesForOrder(supplierOrderId);
        return ResponseEntity.ok(lines);
    }

    @Operation(summary = "Update line quantity", description = "Updates the quantity of a supplier order line")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot update confirmed order"),
        @ApiResponse(responseCode = "404", description = "Line not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<SupplierOrderLineResponseDto> updateLineQuantity(
            @PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal newQuantity = request.get("quantity");
        SupplierOrderLineResponseDto updatedLine = supplierOrderLineService.updateLineQuantity(id, newQuantity);
        return ResponseEntity.ok(updatedLine);
    }

    @Operation(summary = "Remove line from supplier order", description = "Removes a line from a supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Line removed successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot remove from confirmed order"),
        @ApiResponse(responseCode = "404", description = "Line not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeLineFromOrder(@PathVariable Long id) {
        supplierOrderLineService.removeLineFromOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calculate supplier order total", description = "Calculates the total amount for a supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/order/{supplierOrderId}/total")
    public ResponseEntity<Map<String, BigDecimal>> calculateOrderTotal(@PathVariable Long supplierOrderId) {
        BigDecimal total = supplierOrderLineService.calculateOrderTotal(supplierOrderId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @Operation(summary = "Get all supplier order lines", description = "Retrieves all supplier order lines in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier order lines retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/all")
    public ResponseEntity<List<SupplierOrderLineResponseDto>> getAllSupplierOrderLines() {
        List<SupplierOrderLineResponseDto> lines = supplierOrderLineService.getAll();
        return ResponseEntity.ok(lines);
    }
}