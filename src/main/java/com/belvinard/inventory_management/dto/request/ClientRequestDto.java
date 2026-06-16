package com.belvinard.inventory_management.dto.request;

import com.belvinard.inventory_management.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientRequestDto(
        @NotBlank(message = "The client name is required")
        @Size(min = 3, max = 100, message = "The name must contain between 4 and 100 characters")
        String name,

        Address address,

        @Email(message = "The email must be valid")
        String email,

        @Pattern(
                regexp = "^$|^(?:(?:\\+237|237)[-.\\s]?)?(?:[67][25-9]\\d{7}|2\\d{2}\\d{6})$",
                message = "The phone number must be a valid Cameroonian number (mobile or fixed). Examples: 671234567, 222123456, +237-233123456"
        )
        String phoneNumber
) {}

