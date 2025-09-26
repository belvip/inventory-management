package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateClientOrderMovementDto(
        @NotNull(message = "Article ID is mandatory")
        Long articleId,

        @NotNull(message = "Quantity is mandatory")
        @Positive(message = "Quantity must be positive")
        Long quantity,

        @NotNull(message = "Client order ID is mandatory")
        Long clientOrderId,

        @NotBlank(message = "Description is mandatory")
        String description
) {
}