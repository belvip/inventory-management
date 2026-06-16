package com.belvinard.inventory_management.dto.response;

import com.belvinard.inventory_management.dto.AddressDto;

public record UpdateUserResponseDto(
        Long userId,
        String firstName,
        String lastName,
        String userName,
        String email,
        AddressDto address
) {
}
