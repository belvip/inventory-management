package com.belvinard.inventory_management.dto;

import com.belvinard.inventory_management.model.Address;

import java.time.LocalDate;

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
        LocalDate updatedAt
) {
}
