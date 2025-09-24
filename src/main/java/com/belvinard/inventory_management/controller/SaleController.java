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
}