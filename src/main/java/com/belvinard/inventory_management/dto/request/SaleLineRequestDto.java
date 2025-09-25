package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.NotNull;

public record SaleLineRequestDto(
        @NotNull(message = "Order line ID is mandatory")
        Long orderLineId
) {
}