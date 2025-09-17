package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.Address;

public record ClientResponseDto(
        Long id,
        String name,
        Address address,
        String email,
        String phoneNumber,
        String createdAt,
        String updatedAt
) {}

