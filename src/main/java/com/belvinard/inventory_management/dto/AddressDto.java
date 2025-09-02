package com.belvinard.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message = "Address line 1 is required")
        @Size(min = 5, max = 100)
        String address1,

        @Size(min = 4, message = "Address line 2 must have at least 4 characters")
        String address2,

        @NotBlank(message = "City is required")
        @Size(min = 4, max = 50)
        String city,

        @NotBlank(message = "Postal code is required")
        @Size(min = 3, max = 10)
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 50)
        String country
) {}
