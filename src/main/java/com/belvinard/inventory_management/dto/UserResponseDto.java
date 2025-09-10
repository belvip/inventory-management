package com.belvinard.inventory_management.dto;

public record UserResponseDto(
        Long userId,
        String firstName,
        String lastName,
        String userName,
        String email,
        boolean enabled,
        boolean accountNonLocked,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        String roleName,
        String image,
        AddressDto address
) {}
