package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateAccountLockStatusRequest(
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotNull(message = "Lock status is required")
        Boolean lock
) {}