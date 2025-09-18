package com.belvinard.inventory_management.dto.response;

import java.math.BigDecimal;

public record OrderClientLineResponseDto(
        Long id,
        Long articleId,
        String articleCode,
        String articleDesignation,
        BigDecimal unitPriceExclTax,
        BigDecimal rateTva,
        BigDecimal unitPriceAllTax,
        BigDecimal quantity,
        BigDecimal totalLinePrice
) {}
