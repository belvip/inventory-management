package com.belvinard.inventory_management.dto;

import jakarta.validation.constraints.Size;

public record AddressDto(
        @Size(min = 5, max = 100)
        String address1,

        @Size(max = 100)
        String address2,

        @Size(min = 4, max = 50)
        String city,

        @Size(min = 3, max = 10)
        String postalCode,

        @Size(min = 2, max = 50)
        String country
) {}
