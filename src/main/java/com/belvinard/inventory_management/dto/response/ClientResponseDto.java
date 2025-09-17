package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.model.Address;

import java.util.List;

public record ClientResponseDto(
        Long id,
        String name,
        Address address,
        String email,
        String phoneNumber,
        List<ClientOrderResponseDto> orders,
        String createdAt,
        String updatedAt
) {}

