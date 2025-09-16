package com.belvinard.inventory_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ArticleResponseDto(
        String codeArticle,
        String designation,
        Long quantityInStock,
        BigDecimal unitPriceExclTax,
        BigDecimal rateTva,
        BigDecimal unitPriceAllTax,
        String image,
        Long categoryId,
        String categoryDesignation,
        LocalDate createdDate,
        LocalDate updatedDate
) {}
