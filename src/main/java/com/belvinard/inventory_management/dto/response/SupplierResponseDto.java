package com.belvinard.inventory_management.dto.response;

import java.time.LocalDate;

public record SupplierResponseDto(
        Long id,
        String name,
        String phoneNumber,
        LocalDate createdAt,
        LocalDate updatedAt

) {
}
