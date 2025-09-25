package com.belvinard.inventory_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleLineResponseDto(
        Long id,
        Long articleId,
        String articleCode,
        String articleDesignation,
        BigDecimal quantity,
        BigDecimal unitPriceExclTax,
        BigDecimal rateTva,
        BigDecimal unitPriceAllTax,
        BigDecimal totalLinePrice,
        LocalDate createdDate,
        LocalDate updatedDate
) {
}