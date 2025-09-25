package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier", description = "Endpoints for managing suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier - ADMIN or MANAGER",
            description = "Creates a new supplier and returns the created supplier data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Supplier already exists")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<SupplierResponseDto> createSupplier(
            @Valid @RequestBody SupplierResponseDto dto) {
        SupplierResponseDto createdSupplier = supplierService.createSupplier(dto);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }
}
