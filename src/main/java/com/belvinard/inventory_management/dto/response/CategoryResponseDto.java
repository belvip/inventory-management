package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;

public record CategoryResponseDto(
        Long id,
        String designation,
        String code,
        LocalDate createdDate,
        LocalDate updatedDate
) {}
