package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateUserRoleRequest(

        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be greater than 0")
        Long userId,

        @NotBlank(message = "Role name is required")
        String roleName
) {}
