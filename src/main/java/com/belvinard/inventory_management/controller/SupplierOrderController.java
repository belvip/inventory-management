package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.SupplierOrderRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierOrderResponseDto;
import com.belvinard.inventory_management.service.SupplierOrderService;
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

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/supplier-orders")
@RequiredArgsConstructor
@Tag(name = "Supplier Orders Controller", description = "Supplier order management operations")
public class SupplierOrderController {
    private final SupplierOrderService supplierOrderService;
    
    @Operation(summary = "Create a new supplier order", description = "Creates a new supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Supplier order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<SupplierOrderResponseDto> createSupplierOrder(
            @Valid @RequestBody SupplierOrderRequestDto dto) {
        SupplierOrderResponseDto createdOrder = supplierOrderService.create(dto, dto.supplierId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Get supplier order by ID", description = "Retrieves a supplier order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier order found"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrderResponseDto> getSupplierOrderById(@PathVariable Long id) {
        SupplierOrderResponseDto order = supplierOrderService.getById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Update supplier order", description = "Updates supplier order details (not status)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier order updated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot update CONFIRMED order"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierOrderResponseDto> updateSupplierOrder(
            @PathVariable Long id, @Valid @RequestBody SupplierOrderRequestDto dto) {
        SupplierOrderResponseDto updatedOrder = supplierOrderService.update(id, dto);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Delete supplier order", description = "Deletes a supplier order (not allowed for CONFIRMED orders)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Supplier order deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete CONFIRMED order"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplierOrder(@PathVariable Long id) {
        supplierOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update supplier order status", description = "Updates the status of a supplier order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplierOrderResponseDto> updateSupplierOrderStatus(
            @PathVariable Long id, @RequestParam String status) {
        SupplierOrderResponseDto updatedOrder = supplierOrderService.updateState(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Cancel supplier order", description = "Cancels a supplier order (not allowed for CONFIRMED orders)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel CONFIRMED order"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SupplierOrderResponseDto> cancelSupplierOrder(@PathVariable Long id) {
        SupplierOrderResponseDto cancelledOrder = supplierOrderService.cancel(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    @Operation(summary = "Find supplier order by code", description = "Retrieves a supplier order by its code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier order found"),
        @ApiResponse(responseCode = "404", description = "Supplier order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/code/{code}")
    public ResponseEntity<SupplierOrderResponseDto> getSupplierOrderByCode(@PathVariable String code) {
        SupplierOrderResponseDto order = supplierOrderService.findByCode(code);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Get all supplier orders", description = "Retrieves all supplier orders in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier orders retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/all")
    public ResponseEntity<List<SupplierOrderResponseDto>> getAllSupplierOrders() {
        List<SupplierOrderResponseDto> orders = supplierOrderService.getAll();
        return ResponseEntity.ok(orders);
    }
}
