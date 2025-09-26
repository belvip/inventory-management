package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.MvtOrigin;
import com.belvinard.inventory_management.model.StockMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockMovementRequestDto(
        @NotNull(message = "Quantity is mandatory")
        @Positive(message = "Quantity must be positive")
        Long quantity,

        @NotBlank(message = "Description is mandatory")
        String description,

        @NotNull(message = "Movement type is mandatory")
        StockMovementType movementType,

        @NotNull(message = "Movement origin is mandatory")
        MvtOrigin mvtOrigin,

        @NotNull(message = "Article ID is mandatory")
        Long articleId,

        Long sourceId
) {
}