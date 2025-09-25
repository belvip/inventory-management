package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.SupplierRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.service.SupplierService;
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
@RequestMapping("${api.prefix}/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Controller", description = "Endpoints for managing suppliers. Access by ADMIN, MANAGER and SALES")
public class SupplierController {
    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier",
            description = "Creates a new supplier and returns the created supplier data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Supplier already exists")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<SupplierResponseDto> createSupplier(
            @Valid @RequestBody SupplierRequestDto dto) {
        if (dto.companyId() == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        SupplierResponseDto createdSupplier = supplierService.createSupplier(dto, dto.companyId());
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get supplier bi ID"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(
            @Parameter(description = "Supplier ID", example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }


    @Operation(
            summary = "Update supplier",
            description = "Update the details of an existing supplier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier with the same name already exits")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(
            @Parameter(description = "Supplier Id to update", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid SupplierRequestDto dto
    ){
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @Operation(
            summary = "Get all suppliers",
            description = "Retrieve all suppliers in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDto.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers(){
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @Operation(
            summary = "Delete supplier",
            description = "Delete an existing supplier by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Supplier Id to delete", example = "1")
            @PathVariable Long id
    ){
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

}
