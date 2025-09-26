package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.StockMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateManualAdjustmentDto(
        @NotNull(message = "Article ID is mandatory")
        Long articleId,

        @NotNull(message = "Quantity is mandatory")
        @Positive(message = "Quantity must be positive")
        Long quantity,

        @NotNull(message = "Movement type is mandatory")
        StockMovementType type,

        @NotBlank(message = "Description is mandatory")
        String description
) {
}