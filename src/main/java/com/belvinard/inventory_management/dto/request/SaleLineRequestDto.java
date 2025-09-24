package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaleLineRequestDto(
        @NotNull(message = "Article ID is mandatory")
        Long articleId,
        
        @NotNull(message = "Quantity is mandatory")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        BigDecimal quantity
) {
}