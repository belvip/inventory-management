package com.belvinard.inventory_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(

        @NotBlank(message = "The category designation is required")
        @Size(min = 2, max = 100, message = "The designation must contain between 2 and 100 characters")
        String designation,

        @NotBlank(message = "The category code is required")
        @Size(min = 3, max = 10, message = "The code must contain between 3 and 10 characters")
        @Pattern(
                regexp = "CAT-\\d{3}|CAT[A-Z]{3}",
            message = "The code must be in the format CAT-XXX or CATEFT"
        )
        String code,

        @NotNull(message = "The companyId is required")
        Long companyId
) {}
