package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.Address;

import java.time.LocalDate;
import java.util.List;

public record CompanyResponseDto(
        Long id,
        String name,
        String description,
        Address address,
        String fiscalCode,
        String image,
        String email,
        String phoneNumber,
        String website,
        LocalDate createdAt,
        LocalDate updatedAt,
        List<CategoryResponseDto> categories
) {
}
