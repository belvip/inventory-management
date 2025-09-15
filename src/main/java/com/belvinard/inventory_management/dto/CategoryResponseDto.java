package com.belvinard.inventory_management.dto;

import java.time.LocalDate;

public record CategoryResponseDto(
        Long id,
        String designation,
        String code,
        LocalDate createdAt,
        LocalDate updatedAt
) {}
