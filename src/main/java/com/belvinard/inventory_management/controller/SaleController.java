package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.SaleRequestDto;
import com.belvinard.inventory_management.dto.response.SaleResponseDto;
import com.belvinard.inventory_management.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("${api.prefix}/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales management operations")
public class SaleController {

    private final SaleService saleService;

    @Operation(
        summary = "Create a new sale",
        description = "Creates a new sale in DRAFT status. Client must have at least one existing order."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Sale created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SaleResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - Sale status must be DRAFT or client has no orders",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(mediaType = "application/json")
        )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<SaleResponseDto> createSale(
        @Parameter(description = "Sale creation request", required = true)
        @Valid @RequestBody SaleRequestDto saleRequestDto
    ) {
        SaleResponseDto createdSale = saleService.createSale(saleRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    @Operation(
        summary = "Update a sale",
        description = "Updates sale details (comments, date). Status is NOT updated - use updateSaleStatus endpoint. Only allowed for DRAFT sales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale updated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot update CONFIRMED sale"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SaleResponseDto> updateSale(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id,
        @Parameter(description = "Sale update request", required = true) @Valid @RequestBody SaleRequestDto dto
    ) {
        SaleResponseDto updatedSale = saleService.updateSale(id, dto);
        return ResponseEntity.ok(updatedSale);
    }

    @Operation(
        summary = "Delete a sale",
        description = "Deletes a sale. Only allowed for DRAFT sales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sale deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete CONFIRMED sale"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id
    ) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update sale status",
        description = "Updates the status of a sale with validation rules."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<SaleResponseDto> updateSaleStatus(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id,
        @Parameter(description = "New status (DRAFT, CONFIRMED, CANCELLED)", required = true) @RequestParam String status
    ) {
        SaleResponseDto updatedSale = saleService.updateSaleStatus(id, status);
        return ResponseEntity.ok(updatedSale);
    }

    @Operation(
        summary = "Cancel a sale",
        description = "Cancels a sale. Only allowed for DRAFT sales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel CONFIRMED sale"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDto> cancelSale(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id
    ) {
        SaleResponseDto cancelledSale = saleService.cancelSale(id);
        return ResponseEntity.ok(cancelledSale);
    }

    @Operation(
        summary = "Finalize a sale",
        description = "Finalizes a sale by processing stock and generating final code. Only allowed for CONFIRMED sales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale finalized successfully"),
        @ApiResponse(responseCode = "400", description = "Sale must be CONFIRMED before finalization"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/finalize")
    public ResponseEntity<SaleResponseDto> finalizeSale(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id
    ) {
        SaleResponseDto finalizedSale = saleService.finalizeSale(id);
        return ResponseEntity.ok(finalizedSale);
    }

    @Operation(
        summary = "Get sale by ID",
        description = "Retrieves a specific sale by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale found successfully"),
        @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDto> getSaleById(
        @Parameter(description = "Sale ID", required = true) @PathVariable Long id
    ) {
        SaleResponseDto sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }

    @Operation(
        summary = "Get all sales",
        description = "Retrieves all sales in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sales retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping
    public ResponseEntity<List<SaleResponseDto>> getAllSales() {
        List<SaleResponseDto> sales = saleService.getAll();
        return ResponseEntity.ok(sales);
    }
}