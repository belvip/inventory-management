package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.SaleStatus;

import java.time.LocalDate;

public record SaleResponseDto(
        Long id,
        String code,
        String comments,
        LocalDate saleDate,
        SaleStatus status,
        String clientName
) {
}
