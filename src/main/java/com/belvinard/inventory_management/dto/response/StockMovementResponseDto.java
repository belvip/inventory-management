package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.MvtOrigin;
import com.belvinard.inventory_management.model.StockMovementType;

import java.time.LocalDate;

public record StockMovementResponseDto(
        Long id,
        Long quantity,
        String description,
        StockMovementType movementType,
        MvtOrigin mvtOrigin,
        Long articleId,
        String articleCode,
        String articleDesignation,
        Long sourceId,
        LocalDate createdDate,
        LocalDate updatedDate
) {
}