package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;
import java.util.List;

public record CategoryResponseDto(
        Long id,
        String designation,
        String code,
        List<ArticleResponseDto> articles,
        LocalDate createdDate,
        LocalDate updatedDate
) {}
