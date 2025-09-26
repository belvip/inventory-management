package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.ArticleStatus;
import com.belvinard.inventory_management.model.StockMovement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record ArticleResponseDto(
        Long id,
        String codeArticle,
        String designation,
        Long quantityInStock,
        Long reservedQuantity,
        Long availableQuantity,
        BigDecimal unitPriceExclTax,
        BigDecimal rateTva,
        BigDecimal unitPriceAllTax,
        String image,
        Long categoryId,
        ArticleStatus status,
        String categoryDesignation,
        List<StockMovement> stockMovementsList,
        LocalDate createdDate,
        LocalDate updatedDate
) {}
