package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.SaleStatus;
import com.belvinard.inventory_management.validation.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SaleRequestDto(
        @Size(max = 50, message = "Comment must not exceed 50 characters")
        String comments,

        @FutureOrPresent(message = "Sale date cannot be in the past")
        @NotNull(message = "Sale date is mandatory")
        LocalDate saleDate,

        @NotNull(message = "Sale status is required")
        SaleStatus status,

        @NotNull(message = "Client ID is required")
        Long clientId
) {
}

