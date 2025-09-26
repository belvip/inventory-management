package com.belvinard.inventory_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierOrderLineResponseDto(
        Long id,
        Long articleId,
        String articleCode,
        String articleDesignation,
        BigDecimal unitPriceExclTax,
        BigDecimal rateTva,
        BigDecimal unitPriceAllTax,
        BigDecimal quantity,
        BigDecimal totalLinePrice,
        LocalDate createdDate,
        LocalDate updatedDate
) {
}
