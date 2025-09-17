package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.service.ClientOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
@Tag(name = "Client Orders", description = "Endpoints for managing client orders - ADMIN and manager")
public class ClientOrderController {

    private final ClientOrderService clientOrderService;

    @Operation(
            summary = "Create a new client order",
            description = "Creates a new order for a specific client. " +
                          "The order status will always be set to IN_PREPARATION on creation."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error or bad request"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Order code already exists"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<ClientOrderResponseDto> createOrder(
            @Valid
            @RequestBody
            @Parameter(description = "Order data to create a new order", required = true)
            ClientOrderRequestDto requestDto) {

        ClientOrderResponseDto createdOrder = clientOrderService.createOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

}
